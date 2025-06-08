package com.example.mcommerce.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcommerce.R

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Brands()
}

@Composable
fun BrandsCard(
    imageResId: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = title,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(50.dp))
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
fun Brands(){
    val brands = listOf(
        Pair(R.drawable.nike, "Nike"),
        Pair(R.drawable.nike, "Adidas"),
        Pair(R.drawable.nike, "Puma"),
        Pair(R.drawable.nike, "Gucci"),
        Pair(R.drawable.nike, "Zara"),
        Pair(R.drawable.nike, "Active") ,
        Pair(R.drawable.nike, "Nike"),
        Pair(R.drawable.nike, "Adidas"),
        Pair(R.drawable.nike, "Puma"),
        Pair(R.drawable.nike, "Gucci"),
        Pair(R.drawable.nike, "Zara"),
        Pair(R.drawable.nike, "Active")

    )
    Column {
        Text(
            "Top Brands",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(brands) { brand ->
                BrandsCard(
                    imageResId = brand.first,
                    title = brand.second
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBrands() {
    Brands()
}

@Preview
@Composable
private fun PreviewBrandsCard() {
    BrandsCard(
        imageResId = R.drawable.nike,
        title = "Nike"
    )
}

