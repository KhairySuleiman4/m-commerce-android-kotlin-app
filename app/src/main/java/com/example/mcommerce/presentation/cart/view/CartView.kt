package com.example.mcommerce.presentation.cart.view

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.R
import com.example.mcommerce.domain.entities.LineEntity
import com.example.mcommerce.presentation.cart.CartContract
import com.example.mcommerce.presentation.cart.viewmodel.CartViewModel
import com.example.mcommerce.presentation.errors.CartEmptyScreen
import com.example.mcommerce.presentation.errors.FailureScreen
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.theme.Primary
import com.example.mcommerce.presentation.theme.Secondary
import com.shopify.checkoutsheetkit.ColorScheme
import com.shopify.checkoutsheetkit.LogLevel
import com.shopify.checkoutsheetkit.Preloading
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit
import java.util.Locale

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel(),
    navigate: () -> Unit
) {
    val activity = LocalActivity.current as ComponentActivity
    val event = viewModel.events.value
    val state = viewModel.states.value
    val isApply = remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val currency = remember { mutableStateOf("EGP") }
    val rate = remember { mutableDoubleStateOf(1.0) }

    ShopifyCheckoutSheetKit.configure {
        it.colorScheme = ColorScheme.Automatic()
        it.preloading = Preloading(enabled = false)
        it.logLevel = LogLevel.DEBUG
    }

    LaunchedEffect(Unit) {
        viewModel.getCart()
        viewModel.getCurrency()
        viewModel.setEventProcessor(activity, {
            navigate()
        })
    }

    LaunchedEffect(event) {
        when (event) {
            CartContract.Events.DisableApplyEvent -> {
                isApply.value = false
            }

            CartContract.Events.Idle -> {

            }

            is CartContract.Events.DisplayError -> {
                snackbarHostState.showSnackbar(event.msg, duration = SnackbarDuration.Short)
                viewModel.resetEvents()
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
        deleteAction = {
            viewModel.invokeActions(CartContract.Action.ClickOnRemoveItem(it))
        },
        submitAction = {
            viewModel.invokeActions(CartContract.Action.ClickOnSubmit(activity))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
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
    minusAction: (String, Int) -> Unit,
    deleteAction: (String) -> Unit,
    submitAction: () -> Unit
) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf<LineEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    Scaffold(
        modifier = modifier,
        containerColor = Background,
        snackbarHost = {
            SnackbarHost(hostState)
        },
        bottomBar = {
            if (state is CartContract.States.Success && state.cart.items.isNotEmpty()) {
                BottomBar(
                    currency = currency,
                    subtotal = (state.cart.subtotalAmount * rate),
                    discount = (state.cart.discountAmount * rate),
                    total = (state.cart.totalAmount * rate),
                    isApplied = isApplied,
                    onApply = {
                        onApply(it)
                    },
                    onCheckout = {
                        submitAction()
                    }
                )
            }
        }
    ) { padding ->
        when (state) {
            is CartContract.States.Failure -> {
                FailureScreen(state.errorMsg)
            }

            CartContract.States.Idle -> {

            }

            CartContract.States.Loading -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is CartContract.States.Success -> {
                if (state.cart.items.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(state.cart.items.size) {
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
                                },
                                deleteAction = {
                                    showBottomSheet.value = true
                                    selectedItem.value = item
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(320.dp))
                        }
                    }
                } else {
                    CartEmptyScreen()
                }
                if (showBottomSheet.value) {
                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            showBottomSheet.value = false
                            selectedItem.value = null
                        }
                    ) {
                        if (selectedItem.value != null) {
                            val item = selectedItem.value!!
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    fontFamily = PoppinsFontFamily,
                                    text = "Are you sure you want to delete this?",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
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
                                    CartInfoInBottomSheet(
                                        name = item.title,
                                        brand = item.brand,
                                        category = item.category,
                                        currency = currency,
                                        price = item.price * rate
                                    )
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            deleteAction(item.lineId)
                                            selectedItem.value = null
                                            showBottomSheet.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors().copy(
                                            containerColor = Primary,
                                            contentColor = Background,
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20))
                                            .fillMaxWidth(0.5f)
                                    ) {
                                        Text(
                                            fontFamily = PoppinsFontFamily,
                                            text = "Delete"
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            selectedItem.value = null
                                            showBottomSheet.value = false
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors().copy(
                                            contentColor = Primary,
                                        ),
                                        border = BorderStroke(2.dp, Primary),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20))
                                            .fillMaxWidth(0.9f)
                                    ) {
                                        Text(
                                            fontFamily = PoppinsFontFamily, text = "Cancel"
                                        )
                                    }
                                }
                            }
                        }
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
    onApply: (String) -> Unit,
    onCheckout: () -> Unit
) {
    val promoCode = rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(color = Background)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
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
                horizontalArrangement = Arrangement.Start
            ) {
                TextField(
                    value = promoCode.value,
                    onValueChange = {
                        promoCode.value = it
                    },
                    singleLine = true,
                    placeholder = {
                        Text(
                            fontFamily = PoppinsFontFamily,
                            text = "Promo Code or Voucher",
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color(243, 237, 235),
                        unfocusedContainerColor = Color(243, 237, 235)
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .clip(RoundedCornerShape(topStartPercent = 20, bottomStartPercent = 20))
                        .fillMaxWidth(0.7f)
                )
                Spacer(Modifier.width(8.dp))
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
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .width(120.dp)
                        .height(40.dp)
                ) {
                    Text(
                        fontFamily = PoppinsFontFamily,
                        text = "Apply"
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    fontFamily = PoppinsFontFamily,
                    text = "Subtotal",
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
                        fontFamily = PoppinsFontFamily,
                        text = "Discount",
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
                    fontFamily = PoppinsFontFamily,
                    text = "Total",
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
                onCheckout()
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
                fontFamily = PoppinsFontFamily,
                text = "Check out",
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
            fontFamily = PoppinsFontFamily,
            text = currency,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            fontFamily = PoppinsFontFamily,
            text = String.format(locale = Locale.US, "%.2f", price),
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
    deleteAction: () -> Unit
) {
    Card (
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        modifier = modifier
            .fillMaxWidth()
    ){
        Row(
            modifier = modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = item.image,
                contentDescription = item.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(150.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            CartInfo(
                name = item.title,
                brand = item.brand,
                category = item.category,
                currency = currency,
                price = item.price * rate,
                value = item.quantity,
                plusAction = plusAction,
                minusAction = minusAction,
                deleteAction = deleteAction
            )
        }
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
    value: Int,
    plusAction: () -> Unit,
    minusAction: () -> Unit,
    deleteAction: () -> Unit
) {
    val split = category.split("/")
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            fontFamily = PoppinsFontFamily,
            text = name,
            fontSize = 16.sp,
            maxLines = 2
        )

        Text(
            fontFamily = PoppinsFontFamily,
            text = brand,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )

        Text(
            fontFamily = PoppinsFontFamily,
            text = "Size: ${split[0]}",
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Color: ${split[1]}",
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                fontFamily = PoppinsFontFamily,
                text = currency,
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Text(
                fontFamily = PoppinsFontFamily,
                text = String.format(locale = Locale.US, "%.2f", price),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
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
                        Text(
                            fontFamily = PoppinsFontFamily,
                            text ="-"
                        )
                    }
                }
                else{
                    Spacer(
                        Modifier
                            .width(16.dp)
                            .height(16.dp)
                    )
                }
                Text(
                    fontFamily = PoppinsFontFamily,
                    text ="$value",
                    fontSize = 14.sp
                )
                OutlinedIconButton(
                    border = BorderStroke(1.dp, color = Primary),
                    colors = IconButtonDefaults.iconButtonColors().copy( contentColor = Primary),
                    onClick = plusAction,
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                ) {
                    Text(
                        fontFamily = PoppinsFontFamily,
                        text = "+"
                    )
                }
            }
            IconButton(
                colors = IconButtonDefaults.iconButtonColors().copy( contentColor = Primary),
                onClick = deleteAction,
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete_icon),
                    contentDescription = "delete"
                )
            }
        }
    }
}

@Composable
fun CartInfoInBottomSheet(
    name: String,
    brand: String,
    category: String,
    currency: String,
    price: Double,
    modifier: Modifier = Modifier
) {
    val split = category.split("/")
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            fontFamily = PoppinsFontFamily,
            text = name,
            fontSize = 16.sp,
            maxLines = 2
        )

        Text(
            fontFamily = PoppinsFontFamily,
            text = brand,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )

        Text(
            fontFamily = PoppinsFontFamily,
            text = "Size: ${split[0]}",
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Color: ${split[1]}",
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                fontFamily = PoppinsFontFamily,
                text = currency,
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
            Text(
                fontFamily = PoppinsFontFamily,
                text = String.format(locale = Locale.US, "%.2f", price),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview
@Composable
private fun PreviewCartScreen() {
    CartItem(
        item = LineEntity("", 2, 2.0, "", "", "1/x", "", "s"),
        currency = "",
        rate = 1.0,
        plusAction = { },
        minusAction = { },
    ) { }
}