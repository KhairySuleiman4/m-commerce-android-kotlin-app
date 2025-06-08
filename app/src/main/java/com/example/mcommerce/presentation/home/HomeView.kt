package com.example.mcommerce.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.domain.entities.CollectionsEntity

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.getBrands()
    }
    Brands(state = viewModel.states.value)
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
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Brands(modifier: Modifier = Modifier,state: HomeContract.States){
    when(state){
        is HomeContract.States.Failure -> {

        }
        HomeContract.States.Idle -> { }
        HomeContract.States.Loading -> {
            Box(modifier = modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeContract.States.Success -> {
            BrandList(brandsList = state.brandsList)
        }
    }

}

@Composable
fun BrandList(modifier: Modifier = Modifier, brandsList: List<CollectionsEntity>) {
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
                    title = brand.title
                )
            }
        }
    }
}