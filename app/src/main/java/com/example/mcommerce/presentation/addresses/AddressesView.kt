package com.example.mcommerce.presentation.addresses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.presentation.errors.AddressEmptyScreen
import com.example.mcommerce.presentation.errors.FailureScreen
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun AddressesScreen(
    modifier: Modifier = Modifier,
    viewModel: AddressesViewModel = hiltViewModel(),
    navigateTo: () -> Unit
) {
    val event = viewModel.events.value
    val state = viewModel.states.value
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.getAddresses()
    }

    LaunchedEffect(event) {
        when (event) {
            AddressesContract.Events.Idle -> {

            }

            is AddressesContract.Events.ShowError -> {
                snackbarHostState.showSnackbar(event.msg, duration = SnackbarDuration.Short)
            }
        }
    }
    AddressPage(
        modifier = modifier,
        states = state,
        snackbarHostState = snackbarHostState,
        deleteAction = { viewModel.invokeActions(AddressesContract.Action.ClickOnDelete(it)) },
        changeAddress = { viewModel.invokeActions(AddressesContract.Action.ClickOnSetDefault(it)) },
    ) {
        navigateTo()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressPage(
    modifier: Modifier = Modifier,
    states: AddressesContract.States,
    snackbarHostState: SnackbarHostState,
    deleteAction: (String) -> Unit,
    changeAddress: (String) -> Unit,
    addAddress: () -> Unit,
) {
    val currentAddress = remember { mutableStateOf<AddressEntity?>(null) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    when (states) {
        is AddressesContract.States.Failure -> {
            FailureScreen(states.errorMsg)
        }

        AddressesContract.States.Idle -> {

        }

        AddressesContract.States.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is AddressesContract.States.Success -> {
            if (states.data.isNotEmpty())
                currentAddress.value = states.data[0]
            Scaffold(
                modifier = modifier.padding(16.dp),
                containerColor = Background,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = addAddress,
                        containerColor = Primary,
                        contentColor = Background
                    ) {
                        Icon(imageVector = Icons.Default.Add, "add")
                    }
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = if (states.data.size == 1) Arrangement.Center else Arrangement.spacedBy(
                        20.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (states.data.isNotEmpty()) {

                        if (states.data.size > 1) {
                            item {
                                Spacer(Modifier.width(25.dp))
                            }
                        }
                        items(states.data.size) {
                            val address = states.data[it]
                            AddressItem(
                                address = address,
                                onDelete = {
                                    currentAddress.value = address
                                    showBottomSheet.value = true
                                },
                                onChange = {
                                    changeAddress(it)
                                }
                            )
                        }
                        if (states.data.size > 1) {
                            item {
                                Spacer(Modifier.width(25.dp))
                            }
                        }
                    } else {
                        item {
                            AddressEmptyScreen()
                        }
                    }
                }


                if (showBottomSheet.value) {
                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            showBottomSheet.value = false
                        }
                    ) {
                        if (currentAddress.value != null) {
                            val item = currentAddress.value!!
                            Column(
                                Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    fontFamily = PoppinsFontFamily,
                                    textAlign = TextAlign.Center,
                                    text = "Are you sure you want to delete this address?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${currentAddress.value?.name} - ${currentAddress.value?.subName}\n${currentAddress.value?.city}, ${currentAddress.value?.country}",
                                    textAlign = TextAlign.Center,
                                    fontFamily = PoppinsFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Light
                                )
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            deleteAction(item.id)
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
                                            fontFamily = PoppinsFontFamily,
                                            text = "Cancel"
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
fun AddressItem(
    modifier: Modifier = Modifier,
    address: AddressEntity,
    onDelete: (String) -> Unit,
    onChange: (String) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        modifier = if (address.isDefault) {
            modifier
                .border(2.dp, Primary, RoundedCornerShape(10.dp))
                .fillMaxWidth()
        } else {
            modifier
                .fillMaxWidth()
        }
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = if (address.isDefault) Icons.Default.Home else Icons.Default.LocationOn,
                    contentDescription = "Home",
                    modifier = Modifier
                        .size(24.dp),
                    tint = Primary
                )
                Text(
                    text = if (address.isDefault) "Home" else "Address",
                    fontFamily = PoppinsFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold

                )
                if (address.isDefault) {
                    Box(
                        modifier = modifier
                            .background(
                                color = Primary,
                                shape = RoundedCornerShape(50)
                            )
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Default",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = modifier.padding(vertical = 8.dp),
                thickness = 1.dp
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Name",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.width(48.dp))
                    Text(
                        text = address.customerName,
                        fontFamily = PoppinsFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Address",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.width(36.dp))
                    Text(
                        text = "${address.name} - ${address.subName}\n${address.city}, ${address.country}",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Postal code",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = address.zip,
                        fontFamily = PoppinsFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            HorizontalDivider(
                modifier = modifier.padding(vertical = 8.dp),
                thickness = 1.dp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier.fillMaxWidth()
            ) {
                if (!address.isDefault) {
                    TextButton(
                        onClick = { onChange(address.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to Default",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add to Default",
                            fontFamily = PoppinsFontFamily,
                            fontSize = 14.sp,
                            color = Color(0xFF374151)
                        )
                    }
                }
                TextButton(
                    onClick = { onDelete(address.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Add to Default",
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Delete",
                        fontFamily = PoppinsFontFamily,
                        fontSize = 14.sp,
                        color = Color(0xFF374151)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAddress() {
    AddressItem(
        address = AddressEntity(
            id = "1",
            customerName = "Shereen Mohamed",
            name = "Arbaeen",
            subName = "Naser street",
            country = "Egypt",
            city = "Suez",
            zip = "1",
            latitude = 30.11111,
            longitude = 31.11111,
            isDefault = true
        ),
        onDelete = {}
    ) { }
}

@Preview
@Composable
private fun AddressesScreenPreview() {
    AddressesScreen(
        navigateTo = {}
    )
}