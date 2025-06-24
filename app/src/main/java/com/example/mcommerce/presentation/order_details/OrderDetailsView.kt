package com.example.mcommerce.presentation.order_details

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.R
import com.example.mcommerce.data.utils.formatDateTime
import com.example.mcommerce.domain.entities.LineItemEntity
import com.example.mcommerce.domain.entities.OrderEntity
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.Primary
import kotlinx.serialization.json.Json

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetailsScreen(
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    order: String,
    navigateTo: (Screens) -> Unit
) {
    val event = viewModel.events.value

    LaunchedEffect(event) {
        when (event) {
            OrderDetailsContract.Events.Idle -> {}
            is OrderDetailsContract.Events.NavigateToProductInfo -> {
                navigateTo(Screens.ProductDetails(event.productId))
                viewModel.resetEvent()
            }
        }
    }

    OrderDetails(
        order = order,
        onItemClick = { id ->
            viewModel.invokeActions(OrderDetailsContract.Action.ClickOnItem(id))
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetails(
    order: String,
    onItemClick: (String) -> Unit,
) {
    val orderObj = Json.decodeFromString<OrderEntity>(order)
    Log.i("OrderDetailsScreen", ": $orderObj")
    OrderDetailsUI(
        order = orderObj,
        onItemClick
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetailsUI(
    order: OrderEntity,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
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
        }
         item{
             DeliveryStatusCard(order = order)
         }

        item{
            PersonalInfoCard(order = order)
        }

        item{
            ItemsList(
                itemsList = order.lineItems,
                onItemClick = onItemClick
            )
        }

        item{
            PriceDetailsCard(order = order)
        }
    }
}

@Composable
fun PersonalInfoCard(modifier: Modifier = Modifier, order: OrderEntity) {
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
                text = order.customerName,
                fontSize = 16.sp,
            )
            Text(
                text = "${order.city}, ${order.shippingAddress}",
                fontSize = 16.sp,
            )
            Text(
                text = order.phone,
                fontSize = 16.sp,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeliveryStatusCard(modifier: Modifier = Modifier, order: OrderEntity) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(all = 16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.delivery_icon),
                contentDescription = "delivered",
                tint = Primary,
                modifier = modifier.size(40.dp).padding(end = 8.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Delivered",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                val (date, time) = formatDateTime(order.processedAt)
                Text(
                    text = "on $date $time",
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
fun ItemsList(
    itemsList: List<LineItemEntity>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            itemsList.forEach { item ->
                ItemCard(item = item, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
fun PriceDetailsCard(modifier: Modifier = Modifier, order: OrderEntity) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
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
                    text = "${order.subtotalPrice} EGP",
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
                    text = "${order.totalPrice} EGP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    item: LineItemEntity,
    onItemClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onItemClick(item.productId) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = item.imageUrl,
                contentDescription = item.productTitle,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            ItemCardInfo(
                name = item.productTitle,
                brand = item.productTitle,
                size = item.variantTitle,
                currency = "EGP",
                price = item.price
            )
        }
        HorizontalDivider()
    }
}

@Composable
fun ItemCardInfo(
    modifier: Modifier = Modifier,
    name: String,
    brand: String,
    size: String,
    currency: String,
    price: String,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            name.split('|')[1].trim(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                brand.split('|')[0],
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                currency,
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Text(
                price,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}