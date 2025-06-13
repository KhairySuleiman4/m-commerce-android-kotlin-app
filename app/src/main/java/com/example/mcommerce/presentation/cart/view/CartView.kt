package com.example.mcommerce.presentation.cart.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.mcommerce.presentation.cart.models.CartModel
import com.example.mcommerce.presentation.cart.models.LineItem
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun CartScreen(modifier: Modifier = Modifier) {
    CartPage(modifier = modifier)
}

@Composable
fun CartPage(modifier: Modifier = Modifier) {
    val cart = CartModel(
        2000.0,
        1500.0,
        listOf(
            LineItem("First Item", "my brand", "M", 1, 200.0),
            LineItem("Second Item", "my brand", "L", 3, 200.0),

        )
    )
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomBar(
                currency = "EGP",
                subtotal = cart.subtotal,
                total = cart.total
            )
        }
        ) { padding->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(cart.lines.size){
                val item = remember { mutableStateOf(cart.lines[it]) }
                if (item.value.quantity!=0) {
                    CartItem(
                        item = item.value,
                        currency = "EGP",
                        plusAction = {
                            item.value = item.value.copy(quantity = item.value.quantity + 1)
                        },
                        minusAction = {
                            if (item.value.quantity != 0) {
                                item.value = item.value.copy(quantity = item.value.quantity - 1)
                            }
                        }
                    )
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
    total: Double
) {
    val promoCode = remember { mutableStateOf("") }
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

                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Primary,
                        contentColor = Background,
                    ),
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
            "$price",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CartItem(
    modifier: Modifier = Modifier,
    item: LineItem,
    currency: String,
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
        Image(
            painter = painterResource(R.drawable.usa_flag),
            contentDescription = item.name,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        CartInfo(
            name = item.name,
            brand = item.brand,
            size = item.size,
            currency = currency,
            price = item.price
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
    size: String,
    currency: String,
    price: Double,
    ) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            name,
            fontSize = 20.sp
        )

        Text(
            brand,
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )

        Text(
            "Size: $size",
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
                "$price",
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
        OutlinedIconButton(
            border = BorderStroke(1.dp, color = Primary),
            colors = IconButtonDefaults.iconButtonColors().copy( contentColor = Primary),
            onClick = minusAction,
            modifier = Modifier
                .width(20.dp)
                .height(20.dp)
        ) {
            Text("-")
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