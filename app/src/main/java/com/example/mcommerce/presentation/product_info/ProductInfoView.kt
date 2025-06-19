package com.example.mcommerce.presentation.product_info

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.R
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductVariantEntity
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun ProductInfoScreen(
    productId: String,
    modifier: Modifier = Modifier,
    viewModel: ProductInfoViewModel = hiltViewModel()
) {
    val event = viewModel.events.value
    val state = viewModel.states.value

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getProductById(productId)
    }

    LaunchedEffect(event) {
        when(event){
            is ProductInfoContract.Events.Idle -> {}
            is ProductInfoContract.Events.ShowSnackbar -> {
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

    ProductInfoScreenComposable(
        state = state,
        onAddToCartClicked = { variant ->
            viewModel.invokeActions(ProductInfoContract.Action.ClickOnAddToCart(variant))
        },
        onFavoriteClicked = { product ->
            viewModel.invokeActions(ProductInfoContract.Action.ClickOnAddToWishList(product))
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ProductInfoScreenComposable(
    state: ProductInfoContract.States,
    onAddToCartClicked: (ProductVariantEntity) -> Unit,
    onFavoriteClicked: (ProductInfoEntity) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    when(state){
        is ProductInfoContract.States.Failure -> {
            // Show alert and back to the products screen
        }
        is ProductInfoContract.States.Idle -> {}
        is ProductInfoContract.States.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProductInfoContract.States.Success -> {
            ShowProductInfo(
                product = state.product,
                onAddToCartClicked = onAddToCartClicked,
                onFavoriteClicked = onFavoriteClicked,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
fun ShowProductInfo(
    product: ProductInfoEntity,
    onAddToCartClicked: (ProductVariantEntity) -> Unit,
    onFavoriteClicked: (ProductInfoEntity) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val tabs = listOf(
        stringResource(R.string.overview),
        stringResource(R.string.description),
        stringResource(R.string.add_to_cart)
    )

    val selectedTab = remember {
        mutableIntStateOf(0)
    }

    val imagesPagerState = rememberPagerState(pageCount = {
        product.images.size
    })

    Scaffold(
        bottomBar = {
            BottomBar(
                price = product.price.toString(),
                product = product,
                priceUnit = product.priceUnit,
                onFavoriteClicked = onFavoriteClicked,
            )
        }
    ) { padding ->
        Column(
            modifier = modifier.padding(padding)
        ) {
            ConstraintLayout {
                val (imagesCounter, imagesRef) = createRefs()
                HorizontalPager(
                    state = imagesPagerState,
                    modifier = modifier
                        .fillMaxWidth()
                        .constrainAs(imagesRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )
                {
                    ProductImage(product.images[it])
                }
                Text(
                    "${imagesPagerState.currentPage + 1}/${product.images.size}",
                    color = Color.White,
                    modifier = modifier
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(16.dp))
                        .padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                        .constrainAs(imagesCounter) {
                            bottom.linkTo(imagesRef.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            }

            TabRow(
                contentColor = Primary,
                selectedTabIndex = selectedTab.intValue,
                modifier = modifier
                    .fillMaxWidth(),
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier.tabIndicatorOffset(it[selectedTab.intValue]),
                        color = Primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab.intValue == index,
                        onClick = { selectedTab.intValue = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab.intValue) {
                0 -> ProductDetailsSection(
                    productTitle = product.title,
                    productType = product.productType,
                    productVendor = product.vendor,
                )
                1 -> DescriptionSection(product.description)
                2 -> AddToCartSection(
                    variants = product.variants,
                    onAddToCartClicked = onAddToCartClicked,
                    priceUnit = product.priceUnit
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductImage(imageUrl: String, modifier: Modifier = Modifier) {
    GlideImage(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp),
        model = imageUrl,
        contentDescription = "Product Image"
    )
}

@Composable
fun ProductDetailsSection(
    productTitle: String,
    productType: String,
    productVendor: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = productTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = productType,
                    fontSize = 18.sp
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = productVendor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun DescriptionSection(
    description: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            Text(
                modifier = modifier
                    .padding(
                        vertical = 16.dp,
                        horizontal = 16.dp
                    ),
                text = description,
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
            )
        }
    }
}

@Composable
fun AddToCartSection(
    variants: List<ProductVariantEntity>,
    onAddToCartClicked: (ProductVariantEntity) -> Unit,
    priceUnit: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(variants.size){
            VariantRow(
                variants[it],
                onAddToCartClicked = onAddToCartClicked,
                priceUnit = priceUnit
            )
        }
    }
}

@Composable
fun BottomBar(
    price: String,
    priceUnit: String,
    product: ProductInfoEntity,
    onFavoriteClicked: (ProductInfoEntity) -> Unit,
    modifier: Modifier = Modifier
) {

    val isFavorite = remember {
        mutableStateOf(false)
    }

    ConstraintLayout (
        modifier = modifier.fillMaxWidth()
    ){
        val (divider, priceRef, favoriteBtn) = createRefs()
        HorizontalDivider(
            modifier = modifier.constrainAs(divider){
                top.linkTo(parent.top)
            },
            thickness = 1.dp,
            color = Color.Gray
        )

        Text(
            text = "$price $priceUnit",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.constrainAs(priceRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 24.dp)
            }
        )

        IconButton(
            onClick = {
                isFavorite.value = !isFavorite.value
                onFavoriteClicked(product)
            },
            modifier = modifier
                .constrainAs(favoriteBtn) {
                    top.linkTo(priceRef.top)
                    bottom.linkTo(priceRef.bottom)
                    end.linkTo(parent.end, margin = 16.dp)
                }
        ) {
            Icon(
                imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                tint = if (isFavorite.value) Color.Red else Color.DarkGray,
                contentDescription = stringResource(R.string.favorite_icon)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VariantRow(
    variant: ProductVariantEntity,
    priceUnit: String,
    onAddToCartClicked: (ProductVariantEntity) -> Unit,
    modifier: Modifier = Modifier
) {

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (title, price, image, button) = createRefs()
        Text(
            modifier = modifier.constrainAs(title){
                start.linkTo(parent.start, margin = 24.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, margin = 32.dp)
            },
            text = variant.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = modifier.constrainAs(price){
                start.linkTo(title.start)
                top.linkTo(parent.top, margin = 32.dp)
                bottom.linkTo(parent.bottom)
            },
            text = "${variant.price} $priceUnit",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        GlideImage(
            modifier = modifier
                .size(120.dp)
                .constrainAs(image){
                    end.linkTo(parent.end, margin = 32.dp)
                    top.linkTo(parent.top, margin = 8.dp)
                },
            model = variant.imageUrl,
            contentDescription = variant.title
        )
        Button(
            modifier = modifier.constrainAs(button){
                top.linkTo(image.bottom, margin = 8.dp)
                bottom.linkTo(parent.bottom/*, margin = 8.dp*/)
                start.linkTo(image.start)
                end.linkTo(image.end)
            },
            onClick = {
                if(!variant.isSelected)
                    onAddToCartClicked(variant)
            },
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Primary,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            ),
            border = BorderStroke(1.dp, Primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                if(variant.isSelected) "Remove from Cart" else "Add to Cart",
                color = Primary
            )
            Icon(
                if(variant.isSelected) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                modifier = modifier.padding(start = 4.dp),
                contentDescription = "Cart Icon"
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ProductInfoScreenPreview() {
    ProductInfoScreen("gid://shopify/Product/10443052286225")
}



