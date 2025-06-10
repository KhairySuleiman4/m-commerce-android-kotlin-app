package com.example.mcommerce.presentation.productdetails

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.mcommerce.R
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun ProductInfoScreen(productId: String, modifier: Modifier = Modifier) {
    val imagesArr = listOf(
        R.drawable.product_1_image1,
        R.drawable.product_1_image2,
        R.drawable.product_1_image3,
        R.drawable.product_1_image4,
        R.drawable.product_1_image5
    )

    val tabs = listOf(
        stringResource(R.string.details),
        stringResource(R.string.description),
        stringResource(R.string.reviews)
    )
    val selectedTab = remember {
        mutableIntStateOf(0)
    }

    val imagesPagerState = rememberPagerState(pageCount = {
        imagesArr.size
    })

    val isFavorite = remember {
        mutableStateOf(false)
    }

    Scaffold(
        bottomBar = {
            BottomBar()
        }
    ) { padding ->
        Column(
            modifier = modifier.padding(padding)
        ) {
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
                    ProductImage(imagesArr[it])
                }
                Text(
                    "${imagesPagerState.currentPage + 1}/${imagesArr.size}",
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
                        isFavorite.value = !isFavorite.value
                    },
                    modifier = modifier
                        .constrainAs(favoriteBtn) {
                            top.linkTo(parent.top, margin = 24.dp)
                            end.linkTo(parent.end, margin = 24.dp)
                        }
                ) {
                    Icon(
                        imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                        tint = if (isFavorite.value) Color.Red else Color.DarkGray,
                        contentDescription = stringResource(R.string.favorite_icon)
                    )
                }
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
                listOf("Details", "Description", "Reviews").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab.intValue == index,
                        onClick = { selectedTab.intValue = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab.intValue) {
                0 -> ProductDetailsSection()
                1 -> DescriptionSection()
                2 -> ReviewsSection()
            }
        }
    }
}

@Composable
fun ProductImage(imageRes: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp),
        painter = painterResource(imageRes),
        contentDescription = "Product Image"
    )
}

@Composable
fun ProductDetailsSection(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            Row(
                modifier = modifier
                    .padding(
                        vertical = 16.dp,
                        horizontal = 16.dp
                    )
            ){
                Text(
                    "VANS | AUTHENTIC | LO PRO",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    // Title
    // Variants (sizes, colors)
    // Rating
}

@Composable
fun DescriptionSection(modifier: Modifier = Modifier) {
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
                text = "The forefather of the Vans family, the original Vans Authentic was introduced in 1966 and nearly 4 decades later is still going strong, its popularity extending from the original fans - skaters and surfers to all sorts. Now remodeled into a low top lace-up with a slim silhouette, the Vans Authentic Lo Pro features sturdy canvas uppers with lower sidewalls, metal eyelets, and low profile mini waffle outsoles for a lightweight feel",
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
            )
        }
    }
    // Description
}

@Composable
fun ReviewsSection(modifier: Modifier = Modifier) {
    // Reviews
}

@Composable
fun BottomBar(modifier: Modifier = Modifier) {
    val isAddedToCart = remember {
        mutableStateOf(false)
    }

    ConstraintLayout (
        modifier = modifier.fillMaxWidth()
    ){
        val (divider, price, addToCartBtn) = createRefs()
        HorizontalDivider(
            modifier = modifier.constrainAs(divider){
                top.linkTo(parent.top)
            },
            thickness = 1.dp,
            color = Color.Gray
        )
        Text(
            "1460 EGP",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.constrainAs(price) {
                top.linkTo(divider.bottom, margin = 16.dp)
                bottom.linkTo(parent.bottom, margin = 24.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }
        )

        Button(
            onClick = {
                isAddedToCart.value = !isAddedToCart.value
            },
            modifier = modifier
                .constrainAs(addToCartBtn) {
                    top.linkTo(price.top)
                    bottom.linkTo(price.bottom)
                    end.linkTo(parent.end, margin = 16.dp)
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
                if(isAddedToCart.value) "Remove from Cart" else "Add to Cart",
                color = Primary
            )
            Icon(
                if(isAddedToCart.value) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
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
    ProductInfoScreen("")
}



