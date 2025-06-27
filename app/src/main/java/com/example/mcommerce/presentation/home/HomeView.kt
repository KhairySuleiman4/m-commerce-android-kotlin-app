package com.example.mcommerce.presentation.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.R
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.CouponEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.presentation.errors.FailureScreen
import com.example.mcommerce.presentation.favorites.FavoriteDeleteBottomSheet
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.products.ProductCard
import com.example.mcommerce.presentation.products.ProductsContract
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.utils.toProductsEntity
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateTo: (Screens) -> Unit
) {
    val event = viewModel.events.value
    val state = viewModel.states.value

    val isGuest = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val currency = remember { mutableStateOf("EGP") }
    val rate = remember { mutableDoubleStateOf(1.0) }

    LaunchedEffect(event) {
        when (event) {
            is HomeContract.Events.Idle -> {}

            is HomeContract.Events.NavigateToBrandProducts -> {
                navigateTo(Screens.Products(event.brandId, event.brandName))
                viewModel.resetEvent()
            }

            is HomeContract.Events.NavigateToProductDetails -> {
                navigateTo(Screens.ProductDetails(event.productId))
                viewModel.resetEvent()
            }

            is HomeContract.Events.ShowError -> {
            }

            is HomeContract.Events.UpdateCurrency -> {
                currency.value = event.currency
                rate.doubleValue = event.rate
            }

            is HomeContract.Events.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetEvent()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.invokeActions(HomeContract.Action.LoadHomeData)
        isGuest.value = viewModel.isGuest()
    }

    HomeItems(
        state = state,
        currency = currency.value,
        rate = rate.doubleValue,
        isGuest = isGuest.value,
        snackbarHostState = snackbarHostState,
        onBrandClick = { id, name ->
            viewModel.invokeActions(HomeContract.Action.ClickOnBrand(id, name))
        },
        onFavoriteClick = { product ->
            viewModel.invokeActions(HomeContract.Action.ClickOnFavorite(product))
        },
        onProductClick = { id ->
            viewModel.invokeActions(HomeContract.Action.ClickOnProduct(id))
        }
    )
}

@Composable
fun HomeItems(
    state: HomeContract.HomeState,
    isGuest: Boolean,
    currency: String,
    rate: Double,
    snackbarHostState: SnackbarHostState,
    onBrandClick: (String, String) -> Unit,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (ProductsContract.ProductUIModel) -> Unit,
) {
    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            when {
                state.errorMessage != null -> {
                    item {
                        FailureScreen(state.errorMessage)
                    }
                }

                state.brandsLoading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                else -> {

                    item {
                        AdsCarousel(
                            couponsList = state.codes.map {
                                CouponEntity(
                                    it,
                                    description = "Click here to Copy",
                                    title = "Get ${it.filter { x -> x.isDigit() }}% OFF",
                                    imageRes = R.drawable.ad_placeholder
                                )
                            },
                            isLoading = state.codesLoading,
                        )
                    }
                    item {
                        BrandList(
                            brandsList = state.brandsList,
                            onBrandClick = onBrandClick
                        )
                    }
                    item {
                        BestSellersList(
                            title = "Best Sellers",
                            products = state.bestSellersList,
                            isLoading = state.bestSellersLoading,
                            onProductClick = onProductClick,
                            onFavoriteClick = onFavoriteClick,
                            currency = currency,
                            rate = rate,
                            isGuest = isGuest
                        )
                    }
                    item {
                        LatestArrivalsList(
                            title = "Latest Arrivals",
                            products = state.latestArrivals,
                            isLoading = state.latestArrivalsLoading,
                            onProductClick = onProductClick,
                            onFavoriteClick = onFavoriteClick,
                            currency = currency,
                            rate = rate,
                            isGuest = isGuest
                        )
                    }
                }
            }

        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun BrandList(
    modifier: Modifier = Modifier,
    brandsList: List<CollectionsEntity>,
    onBrandClick: (String, String) -> Unit
) {
    Column {
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Top Brands",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(brandsList) { brand ->
                BrandsCard(
                    brand = brand,
                    onBrandClick = onBrandClick
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BrandsCard(
    brand: CollectionsEntity,
    onBrandClick: (String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .size(100.dp)
            .clickable { onBrandClick(brand.id, brand.title) }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            GlideImage(
                model = brand.imageUrl,
                contentDescription = brand.title,
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestSellersList(
    title: String,
    products: List<ProductsEntity>,
    isLoading: Boolean,
    isGuest: Boolean,
    onFavoriteClick: (ProductsContract.ProductUIModel) -> Unit,
    onProductClick: (String) -> Unit,
    currency: String,
    rate: Double
) {

    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedProduct = remember { mutableStateOf<ProductsEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            fontFamily = PoppinsFontFamily,
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyRow(
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 16.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = ProductsContract.ProductUIModel(
                                id = product.id,
                                title = product.title,
                                imageUrl = product.imageUrl,
                                price = product.price,
                                isFavorite = product.isFavorite,
                                productType = product.productType,
                                brand = product.brand
                            ),
                            onFavoriteClick = {
                                if (!it.isFavorite) {
                                    selectedProduct.value = it.toProductsEntity()
                                    showBottomSheet.value = true
                                } else {
                                    onFavoriteClick(it)
                                }
                            },
                            onProductClick = { productId -> onProductClick(productId) },
                            currency = currency,
                            rate = rate,
                            isGuest = isGuest
                        )
                    }
                }
            }
        }
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

@Composable
fun LatestArrivalsList(
    title: String,
    products: List<ProductsEntity>,
    isLoading: Boolean,
    isGuest: Boolean,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (ProductsContract.ProductUIModel) -> Unit,
    currency: String,
    rate: Double
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            fontFamily = PoppinsFontFamily,
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(800.dp)
                ) {
                    CustomLazyVerticalGrid(
                        content = {
                            items(products) { product ->
                                ProductCard(
                                    product = ProductsContract.ProductUIModel(
                                        id = product.id,
                                        title = product.title,
                                        imageUrl = product.imageUrl,
                                        price = product.price,
                                        isFavorite = product.isFavorite,
                                        productType = product.productType,
                                        brand = product.brand
                                    ),
                                    onFavoriteClick = {
                                        onFavoriteClick(it)
                                    },
                                    onProductClick = { productId -> onProductClick(productId) },
                                    currency = currency,
                                    rate = rate,
                                    isGuest = isGuest
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdsCarousel(
    couponsList: List<CouponEntity>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> {
            val pagerState = rememberPagerState(pageCount = { couponsList.size })
            val context = LocalContext.current
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            LaunchedEffect(couponsList.size) {
                if (couponsList.size > 1) {
                    while (true) {
                        delay(3000)
                        val nextPage = (pagerState.currentPage + 1) % couponsList.size
                        pagerState.animateScrollToPage(nextPage)
                    }
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) { page ->
                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            ).absoluteValue

                    val scale = lerp(
                        start = 0.9f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

                    val alpha = lerp(
                        start = 0.7f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

                    CouponsCard(
                        coupon = couponsList[page],
                        modifier = Modifier
                            .clickable {
                                val clip = ClipData.newPlainText("code", couponsList[page].id)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                    )
                }

                if (couponsList.size > 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(couponsList.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(
                                        width = if (isSelected) 20.dp else 8.dp,
                                        height = 8.dp
                                    )
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (isSelected) {
                                            Color(0xFF8c503b)
                                        } else {
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        }
                                    )
                            )
                            if (index < couponsList.size - 1) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CouponsCard(
    coupon: CouponEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFd1b9b1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    fontFamily = PoppinsFontFamily,
                    text = coupon.title,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 2,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    fontFamily = PoppinsFontFamily,
                    text = coupon.description,
                    fontSize = 12.sp,
                    color = Color.Black,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Image(
                painter = painterResource(id = coupon.imageRes),
                contentDescription = coupon.title,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CouponsCardPreview() {
    CouponsCard(
        coupon = CouponEntity(
            id = "1",
            title = "Summer Collection",
            description = "Discover the latest trends in summer fashion with exclusive discounts",
            imageRes = R.drawable.ad_placeholder
        )
    )
}

@Composable
fun CustomLazyVerticalGrid(modifier: Modifier = Modifier, content: LazyGridScope.() -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize(),
        content = content
    )
}