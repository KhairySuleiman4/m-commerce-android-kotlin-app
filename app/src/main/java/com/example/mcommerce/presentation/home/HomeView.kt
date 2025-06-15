package com.example.mcommerce.presentation.home

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.mcommerce.presentation.navigation.Screens
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateTo: (Screens) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getBrands()
    }

    val event = viewModel.events.value
    LaunchedEffect(event) {
        when(event){
            HomeContract.Events.Idle -> {}
            is HomeContract.Events.NavigateToBrandProducts -> {
                navigateTo(Screens.Products(event.brandId, event.brandName))
                viewModel.resetEvent()
            }
        }
    }

    val adsList = listOf(
        CouponEntity(
            id = "1",
            title = "Big Sale Event",
            description = "Up to 70% off on selected items",
            imageRes = R.drawable.ad_placeholder
        ),
        CouponEntity(
            id = "2",
            title = "New Collection",
            description = "Explore our newest arrivals",
            imageRes = R.drawable.ad_placeholder
        ),
        CouponEntity(
            id = "3",
            title = "Weekend Special",
            description = "Exclusive weekend deals just for you",
            imageRes = R.drawable.ad_placeholder
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        AdsCarousel(couponsList = adsList)
        Brands(
            state = viewModel.states.value,
            onBrandClick = { brandId, brandName ->
                viewModel.invokeActions(HomeContract.Action.ClickOnBrand(brandId, brandName))
            }
        )
    }


}

@Composable
fun Brands(
    modifier: Modifier = Modifier,
    state: HomeContract.States,
    onBrandClick: (String, String) -> Unit
    ){
    when(state){
        is HomeContract.States.Failure -> {
            //show alert
        }
        HomeContract.States.Idle -> { }
        HomeContract.States.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeContract.States.Success -> {
            BrandList(
                brandsList = state.brandsList,
                onBrandClick = onBrandClick
                )
        }
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
            "Top Brands",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(brandsList) { brand ->
                BrandsCard(
                    imageUrl = brand.imageUrl,
                    title = brand.title,
                    modifier = modifier.clickable {
                        onBrandClick(brand.id, brand.title)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BrandsCard(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = title,
            modifier = modifier
                .size(70.dp)
        )
    }
}

@Composable
fun AdsCarousel(
    couponsList: List<CouponEntity>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { couponsList.size })

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
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 64.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) { page ->
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            val scale = lerp(
                start = 0.85f,
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

@Preview(showBackground = true)
@Composable
private fun AdsCarouselPreview() {
    val dummyAds = listOf(
        CouponEntity(
            id = "1",
            title = "Summer Sale",
            description = "Up to 50% off on all summer collections",
            imageRes = R.drawable.ad_placeholder
        ),
        CouponEntity(
            id = "2",
            title = "New Arrivals",
            description = "Check out our latest fashion trends",
            imageRes = R.drawable.ad_placeholder
        ),
        CouponEntity(
            id = "3",
            title = "Flash Deal",
            description = "Limited time offer - Buy 1 Get 1 Free",
            imageRes = R.drawable.ad_placeholder
        )
    )

    AdsCarousel(couponsList = dummyAds)
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
                    text = coupon.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = coupon.description,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 2,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Image(
                painter = painterResource(id = coupon.imageRes),
                contentDescription = coupon.title,
                modifier = Modifier
                    .size(120.dp)
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
