package com.example.mcommerce.presentation.favorites

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.Primary
import java.util.Locale

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    navigationTo: (Screens) -> Unit
) {
    val currency = remember { mutableStateOf("EGP") }
    val rate = remember { mutableDoubleStateOf(1.0) }

    val event = viewModel.events.value
    val state = viewModel.states.value

    LaunchedEffect(Unit) {
        viewModel.getFavoriteProducts()
        viewModel.getCurrency()
    }

    LaunchedEffect(event) {
        when (event) {
            is FavoritesContract.Events.Idle -> {}
            is FavoritesContract.Events.NavigateToProductInfo -> {
                navigationTo(Screens.ProductDetails(event.productId))
                viewModel.resetEvent()
            }

            is FavoritesContract.Events.ShowCurrency -> {
                currency.value = event.currency
                rate.doubleValue = event.rate
            }
        }
    }

    Products(
        state = state,
        currency = currency.value,
        rate = rate.doubleValue,
        onProductClick = { id ->
            viewModel.invokeActions(FavoritesContract.Action.ClickOnProduct(id))
        },
        onDeleteFromFavoriteClick = { id ->
            viewModel.invokeActions(FavoritesContract.Action.ClickOnDeleteFromFavorite(id))
        }
    )
}

@Composable
fun Products(
    state: FavoritesContract.States,
    currency: String,
    rate: Double,
    onProductClick: (String) -> Unit,
    onDeleteFromFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is FavoritesContract.States.Failure -> {}
        is FavoritesContract.States.Idle -> {}
        is FavoritesContract.States.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is FavoritesContract.States.Success -> {
            ProductsList(
                productsList = state.products,
                currency = currency,
                rate = rate,
                onProductClick = onProductClick,
                onDeleteFromFavoriteClick = onDeleteFromFavoriteClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsList(
    productsList: List<ProductSearchEntity>,
    currency: String,
    rate: Double,
    onProductClick: (String) -> Unit,
    onDeleteFromFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedProduct = remember { mutableStateOf<ProductSearchEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Box {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(productsList.size) { index ->
                ProductCard(
                    product = productsList[index],
                    currency = currency,
                    rate = rate,
                    onProductClick = onProductClick,
                    onDeleteFromFavoriteClick = {
                        selectedProduct.value = productsList[index]
                        showBottomSheet.value = true
                    }
                )
            }
        }
        if (showBottomSheet.value && selectedProduct.value != null) {
            FavoriteDeleteBottomSheet(
                productId = selectedProduct.value!!.id,
                onConfirmDelete = {
                    onDeleteFromFavoriteClick(it)
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
    product: ProductSearchEntity,
    currency: String,
    rate: Double,
    onProductClick: (String) -> Unit,
    onDeleteFromFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(4.dp)
            .height(350.dp)
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
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = {
                        onDeleteFromFavoriteClick(product.id)
                    },
                    modifier = modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
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
                text = "${product.brand} | ${product.productType}",
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
                    text = "$currency ${String.format(Locale.US, "%.2f", (product.price * rate))}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Spacer(modifier.weight(1f))
            }
            Spacer(modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDeleteBottomSheet(
    productId: String,
    onConfirmDelete: (String) -> Unit,
    onCancel: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onCancel
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Are you sure you want to delete this product from favorites?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onConfirmDelete(productId)
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Primary,
                        contentColor = Background,
                    ),
                    modifier = modifier
                        .clip(RoundedCornerShape(20))
                        .fillMaxWidth(0.5f)
                ) {
                    Text("Delete")
                }
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        contentColor = Primary,
                    ),
                    border = BorderStroke(2.dp, Primary),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .fillMaxWidth(0.9f)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}