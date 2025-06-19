package com.example.mcommerce.presentation.products

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    collectionId: String,
    brandName: String,
    navigationTo: (Screens)-> Unit,
) {
    val event = viewModel.events.value
    val state = viewModel.states.value

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getProducts(collectionId)
    }

    LaunchedEffect(event) {
        when(event){
            ProductsContract.Events.Idle -> {}
            is ProductsContract.Events.NavigateToProductDetails -> {
                navigationTo(Screens.ProductDetails(event.productId))
                viewModel.resetEvent()
            }
            is ProductsContract.Events.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetEvent()
            }
        }
    }

   Products(
       state = state,
       brandName = brandName,
       onProductClick = { productId ->
           viewModel.invokeActions(ProductsContract.Action.ClickOnProduct(productId))
       },
       onFavoriteClick = { productId ->
           viewModel.invokeActions(ProductsContract.Action.ClickOnFavorite(productId))
       },
       onFilterTypeSelected = { productType ->
           viewModel.invokeActions(ProductsContract.Action.OnTypeSelected(productType))
       },
       snackbarHostState = snackbarHostState
   )

}

@Composable
fun Products(
    modifier: Modifier = Modifier,
    state: ProductsContract.States,
    brandName: String,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onFilterTypeSelected: (String?) -> Unit,
    snackbarHostState: SnackbarHostState
    ) {
    when(state){
        is ProductsContract.States.Failure -> {
            //show alert
        }
        ProductsContract.States.Idle -> {}
        ProductsContract.States.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProductsContract.States.Success -> {
                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    ProductTypeFilterChips(
                        selectedProductType = state.selectedProductType,
                        onTypeSelected = onFilterTypeSelected
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ProductsList(
                        productsList = state.filteredProductsList,
                        brandName = brandName,
                        onProductClick =onProductClick,
                        onFavoriteClick = onFavoriteClick,
                        snackbarHostState = snackbarHostState
                    )
                }
        }
    }
}

@Composable
fun ProductsList(
    modifier: Modifier = Modifier,
    productsList: List<ProductsContract.ProductUIModel>,
    brandName: String,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    Box{
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.fillMaxSize()
        ) {

            items(productsList) { product ->
                ProductCard(
                    product = product,
                    brandName = brandName,
                    onFavoriteClick = onFavoriteClick,
                    onProductClick = onProductClick
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: ProductsContract.ProductUIModel,
    brandName: String,
    onFavoriteClick: (String) -> Unit,
    onProductClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(4.dp)
            .height(350.dp)
            .clickable { onProductClick(product.id) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column{
            Box{
                GlideImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onFavoriteClick(product.id) },
                    modifier = modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = modifier.size(30.dp),
                        tint = Color(0xFFD32F2F)
                    )
                }
            }
            Spacer(modifier.height(8.dp))
            Text(
                text = product.title,
                modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 2
            )

            Text(
                text = brandName,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "EGP ${product.price}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProductTypeFilterChips(
    selectedProductType: String?,
    onTypeSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val typesList = listOf("Accessories", "Shoes", "T-Shirts")
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        item {
            FilterChipTheme(
                label = { Text("All") },
                selected = selectedProductType == null,
                onClick = { onTypeSelected(null) }
            )
        }
        items(typesList) { type ->
            FilterChipTheme(
                label = { Text(type) },
                selected = selectedProductType == type,
                onClick = {
                    onTypeSelected(type)
                },
            )
        }
    }
}

@Composable
fun FilterChipTheme(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        colors = SelectableChipColors(
            containerColor = Color.White,
            labelColor = Primary,
            leadingIconColor = Primary,
            trailingIconColor = Primary,
            disabledContainerColor = Color.Gray,
            disabledLabelColor = Color.Gray,
            disabledLeadingIconColor = Color.White,
            disabledTrailingIconColor = Color.White,
            selectedContainerColor = Primary,
            disabledSelectedContainerColor = Color.White,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White,
            selectedTrailingIconColor = Color.White
        ),
        border = BorderStroke(0.5.dp, Primary)
    )
}

