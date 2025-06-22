package com.example.mcommerce.presentation.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.mcommerce.R
import com.example.mcommerce.presentation.navigation.Screens

@Composable
fun OrdersScreen(modifier: Modifier = Modifier, navigateTo: (Screens) -> Unit) {
    OrdersList(navigateTo)
}

@Composable
fun Orders(modifier: Modifier = Modifier) {
    
}

@Composable
fun OrdersList(navigateTo: (Screens) -> Unit) {
    val orders = listOf(
        OrdersContract.OrderUIModel(
            orderName = "#F15306",
            orderDate = "July 29,2023",
            orderTime = "5:30 pm",
            orderPrice = "1560",
            currencyCode = "EGP"
        ),
        OrdersContract.OrderUIModel(
            orderName = "#F15307",
            orderDate = "Aug 01,2023",
            orderTime = "12:00 pm",
            orderPrice = "3250",
            currencyCode = "EGP"
        )
    )
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders) { order ->
            OrderCard(order, navigateTo)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OrderCard(
    order: OrdersContract.OrderUIModel,
    navigateTo: (Screens) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "Order ID: ${order.orderName}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(bottom = 12.dp)
        )
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Color.White),
            modifier = modifier.clickable {
                navigateTo(Screens.OrderDetailsScreen(orderId = order.orderName))
            }.fillMaxWidth()
        ) {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                Row(
                    modifier = modifier.height(120.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.product_5_image1),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 150.dp, height = 120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(start = 16.dp)
                    )
                    //                GlideImage(
                    //                    model = order.imageUrl,
                    //                    contentDescription = order.orderName
                    //                )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Total Amount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "${order.currencyCode} ${order.orderPrice}",
                            fontSize = 14.sp,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 32.dp, bottom = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Date",
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(text = order.orderDate)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Time",
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(text = order.orderTime)
                    }
                }
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun OrdersScreenPreview() {
//    OrdersScreen()
//}