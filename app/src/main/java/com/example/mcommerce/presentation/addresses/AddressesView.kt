package com.example.mcommerce.presentation.addresses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.presentation.theme.Background
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
    val currentAddress = remember { mutableStateOf<String?>(null) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    when (states) {
        is AddressesContract.States.Failure -> {
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
                currentAddress.value = states.data[0].id
            Scaffold(
                modifier = modifier,
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
                LazyRow(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    horizontalArrangement = if(states.data.size == 1) Arrangement.Center else Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(states.data.size) {
                        val address = states.data[it]
                        AddressItem(
                            address = address,
                            onDelete = {
                                currentAddress.value = it
                                showBottomSheet.value = true
                            },
                            onChange = {
                                changeAddress(it)
                            }
                        )
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
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "Are you sure you want to delete this?",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            deleteAction(item)
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
                                        Text("Delete")
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
                                        Text("Cancel")
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
    Column(
        modifier = modifier
            .border(2.dp, Primary,RoundedCornerShape(10.dp))
            .padding(16.dp)
            .fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = if (address.isDefault) Icons.Default.Home else Icons.Default.LocationOn,
            contentDescription = "Home",
            modifier = Modifier
                .width(100.dp)
                .height(100.dp),
            tint = Primary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(0.5f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Street:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(address.name)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Country:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(address.country)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("ZIP:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(address.zip)
                }
                Button(
                    onClick = {
                        onChange(address.id)
                    },
                    enabled = !address.isDefault,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Primary,
                        contentColor = Background,
                        disabledContainerColor = Primary,
                        disabledContentColor = Background
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .fillMaxWidth()
                ) {
                    if (address.isDefault)
                        Text("Current Default")
                    else
                        Text("Set to Default")
                }
            }
            Column(
                Modifier.fillMaxWidth(0.7f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Area:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(address.subName)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("City:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(address.city)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("")
                }
                OutlinedButton(
                    onClick = {
                        onDelete(address.id)

                    },
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        contentColor = Primary,
                    ),
                    border = BorderStroke(2.dp, Primary),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .fillMaxWidth()
                ) {
                    Text("Delete")

                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAddressItem() {
}