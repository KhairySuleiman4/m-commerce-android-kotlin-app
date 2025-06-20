package com.example.mcommerce.presentation.cart.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.domain.entities.LineEntity
import com.example.mcommerce.presentation.cart.CartContract
import com.example.mcommerce.presentation.cart.viewmodel.CartViewModel
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.Primary
import java.util.Locale

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel()
    ) {
    val event = viewModel.events.value
    val state = viewModel.states.value
    val isApply = remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val currency = remember { mutableStateOf("EGP") }
    val rate = remember { mutableDoubleStateOf(1.0) }

    LaunchedEffect(Unit) {
        viewModel.getCart()
        viewModel.getCurrency()
    }

    LaunchedEffect(event) {
        when(event){
            CartContract.Events.DisableApplyEvent -> {
                isApply.value = false
            }
            CartContract.Events.Idle -> {

            }

            is CartContract.Events.DisplayError -> {
                snackbarHostState.showSnackbar(event.msg , duration = SnackbarDuration.Short)
            }

            is CartContract.Events.SetCurrency -> {
                currency.value = event.currency
                rate.doubleValue = event.rate
            }
        }
    }
    CartPage(
        modifier = modifier,
        state = state,
        isApplied = isApply.value,
        currency = currency.value,
        rate = rate.doubleValue,
        hostState = snackbarHostState,
        onApply = {
            viewModel.invokeActions(CartContract.Action.ClickOnApplyDiscount(it))
        },
        plusAction = { id, quantity ->
            viewModel.invokeActions(CartContract.Action.ClickOnPlusItem(id, quantity))
        },
        minusAction = { id, quantity ->
            viewModel.invokeActions(CartContract.Action.ClickOnMinusItem(id, quantity))
        },
    )
}

@Composable
fun CartPage(
    modifier: Modifier = Modifier,
    state: CartContract.States,
    isApplied: Boolean,
    currency: String,
    rate: Double,
    hostState: SnackbarHostState,
    onApply: (String) -> Unit,
    plusAction: (String, Int) -> Unit,
    minusAction: (String, Int) -> Unit
    ) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState)
        },
        bottomBar = {
            if (state is CartContract.States.Success)
            BottomBar(
                currency = currency,
                subtotal = (state.cart.subtotalAmount * rate),
                discount = (state.cart.discountAmount * rate),
                total = (state.cart.totalAmount * rate),
                isApplied = isApplied,
                onApply = {
                    onApply(it)
                }
            )
        }
        ) { padding->
        when(state){
            is CartContract.States.Failure -> {

            }
            CartContract.States.Idle -> {

            }
            CartContract.States.Loading -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CartContract.States.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(state.cart.items.size){
                        val item = state.cart.items[it]
                            CartItem(
                                item = item,
                                currency = currency,
                                rate = rate,
                                plusAction = {
                                    plusAction(item.lineId, item.quantity)
                                },
                                minusAction = {
                                    minusAction(item.lineId, item.quantity)
                                }
                            )
                    }
                }
            }
        }

    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    currency: String,
    subtotal: Double,
    discount: Double,
    total: Double,
    isApplied: Boolean,
    onApply: (String)-> Unit
) {
    val promoCode = rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(color = Background)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = promoCode.value,
                    onValueChange = {
                        promoCode.value = it
                    },
                    singleLine = true,
                    placeholder = {
                        Text("Promo Code or Voucher")
                    },
                    colors = TextFieldDefaults.colors().copy(focusedContainerColor = Color(243,237,235), unfocusedContainerColor = Color(243,237,235)),
                    modifier = Modifier
                        .height(50.dp)
                        .clip(RoundedCornerShape(topStartPercent = 20, bottomStartPercent = 20))
                )
                Button(
                    onClick = {
                        if (promoCode.value.isNotBlank()) {
                            onApply(promoCode.value)
                            promoCode.value = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Primary,
                        contentColor = Background,
                    ),
                    enabled = isApplied,
                    modifier = Modifier.clip(RoundedCornerShape(20))
                ) {
                    Text("Apply")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Subtotal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                PriceInfo(
                    currency = currency,
                    price = subtotal
                )
            }
            if (discount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Discount",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    PriceInfo(
                        currency = currency,
                        price = -discount
                    )
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                PriceInfo(
                    currency = currency,
                    price = total
                )
            }
        }
        Button(
            onClick = {
            },

            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Primary,
                contentColor = Background,
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(20))
                .fillMaxWidth()
        ) {
            Text(
                "Check out",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
fun PriceInfo(
    modifier: Modifier = Modifier,
    currency: String,
    price: Double
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            currency,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            String.format(locale = Locale.US,"%.2f", price),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CartItem(
    modifier: Modifier = Modifier,
    item: LineEntity,
    currency: String,
    rate: Double,
    plusAction: () -> Unit,
    minusAction: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = Background)
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = item.image,
            contentDescription = item.title,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        CartInfo(
            name = item.title,
            brand = item.brand,
            category = item.category,
            currency = currency,
            price = item.price * rate
        )
        QuantityChanger(
            value = item.quantity,
            plusAction = plusAction,
            minusAction = minusAction
        )
    }
}

@Composable
fun CartInfo(
    modifier: Modifier = Modifier,
    name: String,
    brand: String,
    category: String,
    currency: String,
    price: Double,
    ) {
    val split = category.split("/")
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            name,
            fontSize = 20.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(200.dp)
        )

        Text(
            brand,
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )

        Text(
            "Size: ${split[0]}",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            "Color: ${split[1]}",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =Arrangement.spacedBy(8.dp)
        ){
            Text(
                currency,
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 18.sp
            )

            Text(
                String.format(locale = Locale.US,"%.2f", price),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun QuantityChanger(
    modifier: Modifier = Modifier,
    value: Int,
    plusAction: () -> Unit,
    minusAction: () -> Unit,
    ) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (value != 1) {
            OutlinedIconButton(
                border = BorderStroke(1.dp, color = Primary),
                colors = IconButtonDefaults.iconButtonColors().copy(contentColor = Primary),
                onClick = minusAction,
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
            ) {
                Text("-")
            }
        }
        Text(
            "$value",
            fontSize = 15.sp
            )
        OutlinedIconButton(
            border = BorderStroke(1.dp, color = Primary),
            colors = IconButtonDefaults.iconButtonColors().copy( contentColor = Primary),
            onClick = plusAction,
            modifier = Modifier
                .width(20.dp)
                .height(20.dp)
        ) {
            Text("+")
        }
    }
}

@Preview
@Composable
private fun PreviewCartScreen() {
    CartScreen()
}