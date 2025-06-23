package com.example.mcommerce.presentation.products

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.R
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.presentation.favorites.FavoriteDeleteBottomSheet
import com.example.mcommerce.presentation.home.CustomLazyVerticalGrid
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.Primary
import com.example.mcommerce.presentation.utils.toProductsEntity
import java.util.Locale

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    collectionId: String,
    navigationTo: (Screens) -> Unit,
) {

    val currency = remember { mutableStateOf("EGP") }
    val rate = remember { mutableDoubleStateOf(1.0) }

    val event = viewModel.events.value
    val state = viewModel.states.value

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getProducts(collectionId)
        viewModel.getCurrency()
    }

    LaunchedEffect(event) {
        when (event) {
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

            is ProductsContract.Events.ChangeCurrency -> {
                currency.value = event.currency
                rate.doubleValue = event.rate
            }
        }
    }

    Products(
        state = state,
        currency = currency.value,
        rate = rate.doubleValue,
        onProductClick = { productId ->
            viewModel.invokeActions(ProductsContract.Action.ClickOnProduct(productId))
        },
        onFavoriteClick = { product ->
            viewModel.invokeActions(ProductsContract.Action.ClickOnFavorite(product))
        },
        onFilterTypeSelected = { productType ->
            viewModel.invokeActions(ProductsContract.Action.OnTypeSelected(productType))
        },
        snackbarHostState = snackbarHostState,
    )

}

@Composable
fun Products(
    modifier: Modifier = Modifier,
    state: ProductsContract.States,
    currency: String,
    rate: Double,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (ProductsContract.ProductUIModel) -> Unit,
    onFilterTypeSelected: (String?) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    when (state) {
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
                    currency = currency,
                    rate = rate,
                    onProductClick = onProductClick,
                    onFavoriteClick = onFavoriteClick,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsList(
    productsList: List<ProductsContract.ProductUIModel>,
    currency: String,
    rate: Double,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (ProductsContract.ProductUIModel) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedProduct = remember { mutableStateOf<ProductsEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Box {
        CustomLazyVerticalGrid(
            content = {
                items(productsList) { product ->
                    ProductCard(
                        product = product,
                        onFavoriteClick = {
                            if(!it.isFavorite){
                                selectedProduct.value = it.toProductsEntity()
                                showBottomSheet.value = true
                            } else{
                                onFavoriteClick(it)
                            }
                        },
                        onProductClick = onProductClick,
                        currency = currency,
                        rate = rate
                    )
                }
            }
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        if (showBottomSheet.value && selectedProduct.value != null) {
            FavoriteDeleteBottomSheet(
                productId = selectedProduct.value!!.id,
                onConfirmDelete = {
                    selectedProduct.value?.let { product ->
                        onFavoriteClick(
                            ProductsContract.ProductUIModel(
                                id = product.id,
                                title = product.title,
                                imageUrl = product.imageUrl,
                                productType = product.productType,
                                brand = product.brand,
                                price = product.price,
                                isFavorite = product.isFavorite
                            )
                        )
                    }
                    selectedProduct.value = null
                    showBottomSheet.value = false
                },
                onCancel = {
                    selectedProduct.value = null
                    showBottomSheet.value = false
                },
                sheetState = sheetState
            )
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: ProductsContract.ProductUIModel,
    currency: String,
    rate: Double,
    onFavoriteClick: (ProductsContract.ProductUIModel) -> Unit,
    onProductClick: (String) -> Unit
) {

    val isFavorite = remember { mutableStateOf(product.isFavorite) }

    LaunchedEffect(product.isFavorite) {
        isFavorite.value = product.isFavorite
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(4.dp)
            .height(250.dp)
            .width(150.dp)
            .clickable { onProductClick(product.id) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box {
                GlideImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = {
                        val newProduct = product.copy(isFavorite = !product.isFavorite)
                        onFavoriteClick(newProduct)
                        isFavorite.value = !isFavorite.value
                    },
                    modifier = modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        modifier = modifier.size(30.dp),
                        imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                        tint = if (isFavorite.value) Color.Red else Color.DarkGray,
                        contentDescription = stringResource(R.string.favorite_icon)
                    )
                }
            }
            Spacer(modifier.height(8.dp))
            Text(
                text = product.title.split('|')[1].trim(),
                modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2
            )
            Text(
                text = product.title.split('|')[0],
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1
            )
            Spacer(modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "$currency ${
                        String.format(
                            Locale.US,
                            "%.2f",
                            (product.price.toDouble() * rate)
                        )
                    }",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
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

