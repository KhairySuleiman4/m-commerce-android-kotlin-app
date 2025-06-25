package com.example.mcommerce.presentation.errors

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcommerce.R
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun AddressEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.empty_address),
            contentDescription = "empty addresses",
            modifier = Modifier.size(350.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            fontFamily = PoppinsFontFamily,
            text = "No Address Yet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Please add your address for your better experience",
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Primary
        )
    }
}

@Composable
fun OrderEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.empty_orders),
            contentDescription = "empty addresses",
            modifier = Modifier.size(350.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Empty Orders",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            fontFamily = PoppinsFontFamily,
            text = "You haven't placed any orders yet. \n" +
                    "Start shopping now and enjoy a great experience!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Primary
        )
    }
}

@Composable
fun CartEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.empty_orders),
            contentDescription = "empty addresses",
            modifier = Modifier.size(350.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Your Cart is Empty",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Looks like you haven’t added anything to your cart yet.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Primary
        )
    }
}

@Composable
fun FavoriteEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.empty_fav),
            contentDescription = "empty addresses",
            modifier = Modifier.size(350.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            fontFamily = PoppinsFontFamily,
            text = "No favorites",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            fontFamily = PoppinsFontFamily,
            text = "You have nothing on your wishlist yet. \n" +
                    "It's never too late to change it :)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Primary
        )
    }
}
@Composable
fun SearchEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.empty_search),
            contentDescription = "empty search",
            modifier = Modifier.size(250.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            fontFamily = PoppinsFontFamily,
            text = "No results found",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}
