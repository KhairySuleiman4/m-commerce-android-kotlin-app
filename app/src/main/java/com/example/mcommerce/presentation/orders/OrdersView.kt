package com.example.mcommerce.presentation.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcommerce.domain.entities.OrderEntity

@Composable
fun OrdersScreen(modifier: Modifier = Modifier) {
    
}

@Composable
fun Orders(modifier: Modifier = Modifier) {
    
}

@Composable
fun OrdersList(
    orders: List<OrdersContract.OrderUIModel>,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders) { order ->
            OrderCard(order)
        }
    }
}

@Composable
fun OrderCard(
    order: OrdersContract.OrderUIModel,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(
                text = "Order ID: ${order.orderName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Total Amount: ${order.orderPrice} ${order.currencyCode}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(end = 32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date", tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(text = order.orderDate)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Time", tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(text = order.orderTime)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrdersListPreview() {
    val dummyOrders = listOf(
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
    OrdersList(dummyOrders)
}