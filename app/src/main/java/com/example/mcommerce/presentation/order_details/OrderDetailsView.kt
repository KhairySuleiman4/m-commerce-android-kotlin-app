package com.example.mcommerce.presentation.order_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.mcommerce.R
import com.example.mcommerce.presentation.cart.models.LineItem
import com.example.mcommerce.presentation.orders.OrdersScreen

@Composable
fun OrderDetailsScreen(modifier: Modifier = Modifier, orderId: String) {
    OrderDetails()
}

@Composable
fun OrderDetails(
    modifier: Modifier = Modifier,
) {
    val order  = OrderDetailsContract.OrderDetailsUIModel(
        name = "#F15306",
        date = "July 29,2023",
        time = "5:30 pm",
        totalPrice = "1560",
        subtotalPrice = "1440",
        discount = "120",
        currencyCode = "EGP",
        userAddress = "Naser street",
        userCity = "Suez",
        userName = "Shereen Mohamed",
        userPhone = "+201569985471",
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Order ID ",
                fontSize = 20.sp,
                modifier = modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier.width(8.dp))

            Text(
                text = order.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Color.White),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "",
                    tint = Color.Green,
                    modifier = modifier.padding(horizontal = 16.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Delivered",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "on ${order.date.split(',')[0]} ${order.time}",
                        fontSize = 16.sp,
                    )
                }
            }
        }

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Color.White),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier.padding(16.dp)
            ) {
                Text(
                    text = "Delivery address",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = order.userName,
                    fontSize = 16.sp,
                )
                Text(
                    text = "${order.userCity}, ${order.userAddress}",
                    fontSize = 16.sp,
                )
                Text(
                    text = order.userPhone,
                    fontSize = 16.sp,
                )
            }
        }

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Color.White),
            modifier = modifier.fillMaxWidth()
        ) {
            Column {
                CartItem(
                    item = LineItem(
                        name = "VANS | SK8-HI DECON (CUTOUT)| LEAVES/WHITE",
                        brand = "Vans",
                        size = "4 / white",
                        quantity = 1,
                        price = 179.95
                    ),
                    currency = "EGP"
                )
                CartItem(
                    item = LineItem(
                        name = "VANS | SK8-HI DECON (CUTOUT)| LEAVES/WHITE",
                        brand = "Vans",
                        size = "4 / white",
                        quantity = 1,
                        price = 179.95
                    ),
                    currency = "EGP"
                )
            }
        }
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Color.White),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.padding(16.dp)
            ) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Subtotal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "${order.subtotalPrice} ${order.currencyCode}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }

                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Discount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "-${order.discount} ${order.currencyCode}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }

                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${order.totalPrice} ${order.currencyCode}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    }
}

@Composable
fun CartItem(
    modifier: Modifier = Modifier,
    item: LineItem,
    currency: String,
) {
    Column {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.product_5_image1),
                contentDescription = item.name,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            CartInfo(
                name = item.name,
                brand = item.brand,
                size = item.size,
                currency = currency,
                price = item.price
            )
        }
        HorizontalDivider()
    }
}

@Composable
fun CartInfo(
    modifier: Modifier = Modifier,
    name: String,
    brand: String,
    size: String,
    currency: String,
    price: Double,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                brand,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.7f),
            )
            Text(
                size,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =Arrangement.spacedBy(8.dp)
        ){
            Text(
                currency,
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Text(
                "$price",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun OrdersScreenPrev() {
//    OrderDetailsScreen()
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun CartItemPrev() {
//    CartItem(
//        item = LineItem(
//            name = "VANS | SK8-HI DECON (CUTOUT)| LEAVES/WHITE",
//            brand = "Vans",
//            size = "4 / white",
//            quantity = 1,
//            price = 179.95
//        ),
//        currency = "EGP"
//    )
//}