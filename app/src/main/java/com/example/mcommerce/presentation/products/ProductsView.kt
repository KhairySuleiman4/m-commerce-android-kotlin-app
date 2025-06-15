package com.example.mcommerce.presentation.products

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.presentation.navigation.Screens

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    brandId: String,
    brandName: String,
    navigationTo: (Screens)-> Unit,
) {
    val event = viewModel.events.value
    val state = viewModel.states.value

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getProducts(brandId)
    }

    LaunchedEffect(event) {
        when(event){
            ProductsContract.Events.Idle -> {}
            is ProductsContract.Events.NavigateToProductDetails -> {
                navigationTo(Screens.ProductDetails(event.productId))
                viewModel.resetEvent()
            }
            is ProductsContract.Events.ShowSnackbar -> {
                val result = snackbarHostState.showSnackbar(
                    message = event.message,
                    actionLabel = "Undo"
                )
                if (result == SnackbarResult.ActionPerformed) {
                    // undo adding or removing from wishlist or cart
                    Log.d("snackbar", "undo clicked")
                }
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
       onAddToCartClick = { productId ->
           viewModel.invokeActions(ProductsContract.Action.ClickOnAddToCart(productId))
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
    onAddToCartClick: (String) -> Unit,
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
            ProductsList(
                productsList = state.productsList,
                brandName = brandName,
                onProductClick =onProductClick,
                onFavoriteClick = onFavoriteClick,
                onAddToCartClick = onAddToCartClick,
                snackbarHostState = snackbarHostState
            )
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
    onAddToCartClick: (String) -> Unit,
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
                    onAddToCartClick = onAddToCartClick,
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
    onAddToCartClick: (String) -> Unit,
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
                Spacer(modifier.weight(1f))
                IconButton(
                    onClick = { onAddToCartClick(product.id) },
                    modifier = modifier
                        .background(Color(0xFF795548), shape = CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = if(product.isInCart) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "cart",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier.height(8.dp))
        }
    }
}
