package com.example.mcommerce.presentation.settings.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.R
import com.example.mcommerce.presentation.settings.SettingsContract
import com.example.mcommerce.presentation.settings.viewmodel.SettingsViewModel
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.showCurrency()
    }
    val event = viewModel.events.value
    val checkedDarkMode = remember { mutableStateOf(false) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val selectCurrency = remember { mutableStateOf("EGP") }
    LaunchedEffect(event) {
        when (event) {
            SettingsContract.Events.CheckDarkMode -> {
                checkedDarkMode.value = true
            }

            SettingsContract.Events.HideCurrencyCatalog -> {
                showBottomSheet.value = false
            }

            SettingsContract.Events.Idle -> {

            }

            is SettingsContract.Events.SaveCurrency -> {
                Toast.makeText(
                    context,
                    "Changes Saved!\nNow ${event.currency} is your displayed currency",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is SettingsContract.Events.SelectCurrency -> {
                selectCurrency.value = event.currency
            }

            SettingsContract.Events.ShowCurrencyCatalog -> {
                showBottomSheet.value = true
            }

            SettingsContract.Events.UnCheckDarkMode -> {
                checkedDarkMode.value = false
            }

            is SettingsContract.Events.ShowError -> {
                Toast.makeText(
                    context,
                    event.msg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    SettingsPage(
        modifier,
        checkedDarkMode = checkedDarkMode.value,
        showBottomSheet = showBottomSheet.value,
        selectedCurrency = selectCurrency.value,
        openSheet = { viewModel.invokeActions(SettingsContract.Action.ClickOnCurrency) },
        checkBoxAction = { viewModel.invokeActions(SettingsContract.Action.ClickOnCheckButton(it)) },
        hideSheet = { viewModel.invokeActions(SettingsContract.Action.ClickOnHide) },
        currencySelection = {
            viewModel.invokeActions(
                SettingsContract.Action.ClickOnSelectedCurrency(
                    it
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    checkedDarkMode: Boolean,
    showBottomSheet: Boolean,
    selectedCurrency: String,
    openSheet: () -> Unit,
    checkBoxAction: (Boolean) -> Unit,
    hideSheet: () -> Unit,
    currencySelection: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    openSheet()
                }
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SettingTabInfo(text = "Currency", image = R.drawable.currency_icon)
            CurrencyDisplay(text = selectedCurrency)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SettingTabInfo(text = "Dark mode", image = R.drawable.dark_mode_icon)
            Checkbox(
                checked = checkedDarkMode,
                onCheckedChange = { checkBoxAction(!it) },
                colors = CheckboxDefaults
                    .colors()
                    .copy(
                        checkedBoxColor = Primary,
                        checkedBorderColor = Primary,
                        uncheckedBorderColor = Primary
                    )
            )
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = {
                hideSheet()
            }
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clickable {
                            currencySelection("USD")
                        }
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CountryTabInfo(text = "US Dollar", image = R.drawable.usa_flag)
                    if (selectedCurrency == "USD")
                        Text(
                            "✔",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    else
                        Text("")
                }
                Row(
                    modifier = Modifier
                        .clickable {
                            currencySelection("EGP")
                        }
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CountryTabInfo(text = "EG Pound", image = R.drawable.egypt_flag)
                    if (selectedCurrency == "EGP")
                        Text(
                            "✔",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    else
                        Text("")
                }
            }
        }
    }
}

@Composable
fun SettingTabInfo(
    modifier: Modifier = Modifier,
    image: Int,
    text: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = text,
            modifier = Modifier
                .width(25.dp)
                .height(25.dp)
        )

        Text(
            text,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}

@Composable
fun CountryTabInfo(
    modifier: Modifier = Modifier,
    image: Int,
    text: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = text,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .width(50.dp)
                .height(50.dp)
        )

        Text(
            text,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun CurrencyDisplay(
    modifier: Modifier = Modifier,
    text: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            color = Color(0, 0, 0, 77),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            ">",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    }
}