package com.example.mcommerce.presentation.orders

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.mcommerce.domain.entities.OrderEntity
import com.example.mcommerce.presentation.errors.FailureScreen
import com.example.mcommerce.presentation.errors.OrderEmptyScreen
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.theme.Primary
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel(),
    navigateTo: (Screens) -> Unit
) {
    val currency = remember { mutableStateOf("EGP") }
    val rate = remember { mutableDoubleStateOf(0.0) }
    val event = viewModel.events.value
    val state = viewModel.states.value

    LaunchedEffect(Unit) {
        viewModel.getOrders()
        viewModel.getCurrency()
    }

    LaunchedEffect(event) {
        when (event) {
            OrdersContract.Events.Idle -> {}
            is OrdersContract.Events.NavigateToOrderDetails -> {
                navigateTo(Screens.OrderDetailsScreen(event.order))
                viewModel.resetEvent()
            }

            is OrdersContract.Events.ShowCurrency -> {
                currency.value = event.currency
                rate.doubleValue = event.rate
                viewModel.resetEvent()
            }
        }
    }

    Orders(
        state = state,
        onOrderClick = { order ->
            viewModel.invokeActions(OrdersContract.Action.ClickOnOrder(order))

        },
        currency = currency.value,
        rate = rate.doubleValue
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Orders(
    state: OrdersContract.States,
    currency: String,
    rate: Double,
    onOrderClick: (OrderEntity) -> Unit
) {
    when (state) {
        is OrdersContract.States.Failure -> {
            FailureScreen(state.errorMessage)
        }

        OrdersContract.States.Idle -> {}
        OrdersContract.States.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is OrdersContract.States.Success -> {
            if (state.ordersList.isNotEmpty())
                OrdersList(
                    ordersList = state.ordersList,
                    onOrderClick = onOrderClick,
                    currency = currency,
                    rate = rate
                )
            else
                OrderEmptyScreen()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersList(
    ordersList: List<OrderEntity>,
    currency: String,
    rate: Double,
    onOrderClick: (OrderEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ordersList) { order ->
            OrderCard(
                order,
                rate = rate,
                onOrderClick = onOrderClick,
                currency = currency,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OrderCard(
    order: OrderEntity,
    currency: String,
    rate: Double,
    onOrderClick: (OrderEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Order ID: ${order.name}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(bottom = 12.dp)
        )
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Color.White),
            modifier = modifier
                .clickable {
                    onOrderClick(order)
                }
                .fillMaxWidth()
        ) {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                Row(
                    modifier = modifier.height(120.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlideImage(
                        model = order.lineItems[0].imageUrl,
                        contentDescription = order.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 150.dp, height = 120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(start = 16.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text(
                            fontFamily = PoppinsFontFamily,
                            text = "Total Amount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            fontFamily = PoppinsFontFamily,
                            text = "$currency ${
                                String.format(
                                    Locale.US,
                                    "%.2f", (order.totalPrice.toDouble() * rate)
                                )
                            }",
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
                    val (date, time) = formatDateTime(order.processedAt)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.date_icon),
                            contentDescription = "Date",
                            tint = Primary,
                            modifier = modifier.size(25.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            fontFamily = PoppinsFontFamily, text = date
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.time_icon),
                            contentDescription = "Time",
                            tint = Primary,
                            modifier = modifier.size(30.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(text = time)
                    }
                }
            }
        }
    }
}