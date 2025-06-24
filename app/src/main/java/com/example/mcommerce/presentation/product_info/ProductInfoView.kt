package com.example.mcommerce.presentation.product_info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.R
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductVariantEntity
import com.example.mcommerce.presentation.errors.FailureScreen
import com.example.mcommerce.presentation.favorites.FavoriteDeleteBottomSheet
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.theme.Primary
import java.util.Locale

@Composable
fun ProductInfoScreen(
    productId: String,
    viewModel: ProductInfoViewModel = hiltViewModel()
) {
    val currency = remember { mutableStateOf("EGP") }
    val rate = remember { mutableDoubleStateOf(1.0) }
    val isGuest = remember { mutableStateOf(false) }

    val event = viewModel.events.value
    val state = viewModel.states.value

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getProductById(productId)
        viewModel.getCurrency()
        isGuest.value = viewModel.isGuest()
    }

    LaunchedEffect(event) {
        when (event) {
            is ProductInfoContract.Events.Idle -> {}
            is ProductInfoContract.Events.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetEvent()
            }

            is ProductInfoContract.Events.ShowCurrency -> {
                currency.value = event.currency
                rate.doubleValue = event.rate
            }
        }
    }

    ProductInfoScreenComposable(
        state = state,
        currency = currency.value,
        rate = rate.doubleValue,
        onAddToCartClicked = { variant ->
            viewModel.invokeActions(ProductInfoContract.Action.ClickOnAddToCart(variant))
        },
        onFavoriteClicked = { product ->
            viewModel.invokeActions(ProductInfoContract.Action.ClickOnAddToWishList(product))
        },
        snackbarHostState = snackbarHostState,
        isGuest = isGuest.value
    )
}

@Composable
fun ProductInfoScreenComposable(
    state: ProductInfoContract.States,
    currency: String,
    rate: Double,
    isGuest: Boolean,
    onAddToCartClicked: (ProductVariantEntity) -> Unit,
    onFavoriteClicked: (ProductInfoEntity) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    when (state) {
        is ProductInfoContract.States.Failure -> {
            FailureScreen(state.errorMessage)
        }

        is ProductInfoContract.States.Idle -> {}
        is ProductInfoContract.States.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ProductInfoContract.States.Success -> {
            ShowProductInfo(
                state = state,
                currency = currency,
                rate = rate,
                onAddToCartClicked = onAddToCartClicked,
                onFavoriteClicked = onFavoriteClicked,
                snackbarHostState = snackbarHostState,
                isGuest = isGuest
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowProductInfo(
    state: ProductInfoContract.States.Success,
    currency: String,
    rate: Double,
    isGuest: Boolean,
    onAddToCartClicked: (ProductVariantEntity) -> Unit,
    onFavoriteClicked: (ProductInfoEntity) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val product = state.product
    val tabs = listOf(
        stringResource(R.string.details),
        stringResource(R.string.add_to_cart)
    )

    val selectedTab = remember { mutableIntStateOf(0) }

    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedProduct = remember { mutableStateOf<ProductInfoEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Box {
        Column {

            ProductImageSection(
                product = product,
                onFavoriteClick = {
                    if (!it.isFavorite) {
                        selectedProduct.value = it
                        showBottomSheet.value = true
                    } else {
                        onFavoriteClicked(it)
                    }
                },
                isGuest = isGuest
            )

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
                        text = {
                            Text(
                                fontFamily = PoppinsFontFamily, text = title
                            )
                        }
                    )
                }
            }

            when (selectedTab.intValue) {
                0 -> ProductDetailsSection(
                    productTitle = product.title,
                    productType = product.productType,
                    productVendor = product.vendor,
                    priceUnit = currency,
                    productPrice = String.format(Locale.US, "%.2f", (product.price * rate)),
                    productDescription = product.description
                )

                1 -> AddToCartSection(
                    variants = product.variants,
                    onAddToCartClicked = onAddToCartClicked,
                    priceUnit = currency,
                    rate = rate
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        if (showBottomSheet.value && selectedProduct.value != null) {
            FavoriteDeleteBottomSheet(
                productId = selectedProduct.value!!.id,
                onConfirmDelete = {
                    selectedProduct.value?.let { product ->
                        onFavoriteClicked(product)
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

@Composable
fun ProductImageSection(
    product: ProductInfoEntity,
    isGuest: Boolean,
    onFavoriteClick: (ProductInfoEntity) -> Unit,
    modifier: Modifier = Modifier
) {

    val imagesPagerState = rememberPagerState(pageCount = { product.images.size })

    val isFavorite = remember { mutableStateOf(product.isFavorite) }

    LaunchedEffect(product.isFavorite) {
        isFavorite.value = product.isFavorite
    }

    ConstraintLayout {
        val (imagesCounter, imagesRef, favoriteBtn) = createRefs()
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
            fontFamily = PoppinsFontFamily,
            text = "${imagesPagerState.currentPage + 1}/${product.images.size}",
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
        IconButton(
            onClick = {
                if (!isGuest) {
                    isFavorite.value = !isFavorite.value
                    val newProduct = product.copy(isFavorite = !product.isFavorite)
                    onFavoriteClick(newProduct)
                } else {
                    onFavoriteClick(product.copy(isFavorite = true))
                }
            },
            modifier = modifier
                .constrainAs(favoriteBtn) {
                    top.linkTo(parent.top, margin = 16.dp)
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
    productPrice: String,
    priceUnit: String,
    productDescription: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    fontFamily = PoppinsFontFamily,
                    text = productTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 2,
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            top = 8.dp
                        )
                )
                Text(
                    fontFamily = PoppinsFontFamily,
                    modifier = modifier
                        .padding(horizontal = 4.dp),
                    text = "$productPrice $priceUnit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        item {
            Text(
                fontFamily = PoppinsFontFamily,
                modifier = modifier
                    .padding(
                        top = 8.dp
                    ),
                text = productType,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        item {
            Text(
                fontFamily = PoppinsFontFamily,
                modifier = modifier
                    .padding(
                        top = 8.dp
                    ),
                text = productVendor,
                fontSize = 18.sp
            )
        }

        item {
            Text(
                fontFamily = PoppinsFontFamily,
                modifier = modifier
                    .padding(
                        top = 8.dp
                    ),
                text = "Description",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                fontFamily = PoppinsFontFamily,
                modifier = modifier
                    .padding(
                        top = 4.dp
                    ),
                text = productDescription,
                fontSize = 18.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun AddToCartSection(
    variants: List<ProductVariantEntity>,
    onAddToCartClicked: (ProductVariantEntity) -> Unit,
    priceUnit: String,
    rate: Double,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(variants.size) {
            VariantRow(
                variants[it],
                onAddToCartClicked = onAddToCartClicked,
                priceUnit = priceUnit,
                rate = rate
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VariantRow(
    variant: ProductVariantEntity,
    priceUnit: String,
    rate: Double,
    onAddToCartClicked: (ProductVariantEntity) -> Unit,
    modifier: Modifier = Modifier
) {

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (title, price, image, button) = createRefs()
        Text(
            fontFamily = PoppinsFontFamily,
            modifier = modifier.constrainAs(title) {
                start.linkTo(parent.start, margin = 24.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, margin = 32.dp)
            },
            text = variant.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            fontFamily = PoppinsFontFamily,
            modifier = modifier.constrainAs(price) {
                start.linkTo(title.start)
                top.linkTo(parent.top, margin = 32.dp)
                bottom.linkTo(parent.bottom)
            },
            text = "${
                String.format(
                    Locale.US,
                    "%.2f",
                    (variant.price.toDouble() * rate)
                )
            } $priceUnit",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        GlideImage(
            modifier = modifier
                .size(120.dp)
                .constrainAs(image) {
                    end.linkTo(parent.end, margin = 32.dp)
                    top.linkTo(parent.top, margin = 8.dp)
                },
            model = variant.imageUrl,
            contentDescription = variant.title
        )
        Button(
            modifier = modifier.constrainAs(button) {
                top.linkTo(image.bottom, margin = 8.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(image.start)
                end.linkTo(image.end)
            },
            onClick = {
                if (!variant.isSelected)
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
                fontFamily = PoppinsFontFamily,
                text = if (variant.isSelected) "Added to Cart" else "Add to Cart",
                color = Primary
            )
            Icon(
                if (variant.isSelected) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                modifier = modifier.padding(start = 4.dp),
                contentDescription = "Cart Icon"
            )
        }
    }
}