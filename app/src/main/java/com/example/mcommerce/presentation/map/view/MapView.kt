package com.example.mcommerce.presentation.map.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.presentation.map.MapContract
import com.example.mcommerce.presentation.map.viewmodel.MapViewModel
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.Primary
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    popup:() -> Unit
    ) {
    val address = remember { mutableStateOf<AddressEntity?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(address.value?.latitude ?: 2.0, address.value?.longitude ?: 3.0), 10f)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    LaunchedEffect(Unit) {
        viewModel.getSelectedLocation(30.1,32.3)
    }

    val event = viewModel.events.value
    LaunchedEffect(event) {
        when(event){
            is MapContract.Events.ChangedAddress -> {
                address.value = event.address
                cameraPositionState.position =  CameraPosition.fromLatLngZoom(LatLng(address.value?.latitude ?: 2.0, address.value?.longitude ?: 3.0), cameraPositionState.position.zoom)
            }
            MapContract.Events.Idle -> {

            }
            is MapContract.Events.ShowError -> {
                snackbarHostState.showSnackbar(message = event.errorMessage)
            }

            MapContract.Events.SavedAddress -> {
                popup()
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                showBottomSheet.value = true
                          },
                containerColor = Primary,
                contentColor = Background
                ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        MapPage(
            modifier = modifier.padding(1.dp),
            state = viewModel.states.value,
            address = LatLng(address.value?.latitude ?: 2.0, address.value?.longitude ?: 3.0),
            cameraPositionState = cameraPositionState,
            searchAction = {
                viewModel.invokeActions(  MapContract.Action.SearchPlace(it) )
            },
            selectMapAction = { latitude, longitude ->
                viewModel.invokeActions(MapContract.Action.ClickOnMapLocation(latitude, longitude))
            }
        ) {
            viewModel.invokeActions(MapContract.Action.ClickOnResult(it))
        }
        if (showBottomSheet.value){
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    showBottomSheet.value = false
                }
            ){
                SheetDesignInput(
                    addressEntity = address.value,
                    saveAction = {
                        viewModel.invokeActions(MapContract.Action.ClickOnSave(it))
                    },
                    dismissAction = {
                        showBottomSheet.value = false
                    }
                )
            }
        }

    }


}

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    state: MapContract.States,
    address: LatLng,
    cameraPositionState: CameraPositionState,
    searchAction: (String)-> Unit,
    selectMapAction: (Double, Double)-> Unit,
    selectAction: (String)-> Unit
) {
    val markerState = rememberUpdatedMarkerState(position = address)
    val text = remember { mutableStateOf("") }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        onMapClick = {
            selectMapAction(it.latitude,it.longitude)
        },
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = markerState,
            title = "Singapore",
            snippet = "Marker in Singapore"
        )
    }

    /*LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            SearchBar(text = text) {
                searchAction(it)
            }
        }

        when(state){
            is MapContract.States.Failure -> {

            }
            MapContract.States.Idle -> {

            }
            MapContract.States.Loading -> {

            }
            is MapContract.States.Success -> {
                items(state.addressList.size){
                    SearchResult(
                        modifier = Modifier
                            .clickable {
                                selectAction(state.addressList[it].first)
                                text.value=""
                            },
                        title = state.addressList[it].second,
                        subtitle = state.addressList[it].third
                    )
                }
            }
        }

    }*/
}

@OptIn(FlowPreview::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    debounceMillis: Long = 500L,
    text: MutableState<String>,
    onDebouncedTextChange: (String) -> Unit,

) {
    val coroutineScope = rememberCoroutineScope()

    val textFlow = remember { MutableStateFlow("") }
    LaunchedEffect(Unit) {
        textFlow
            .debounce(debounceMillis)
            .distinctUntilChanged()
            .collect { debouncedText ->
                if (debouncedText.isNotBlank())
                onDebouncedTextChange(debouncedText)
            }
    }

    TextField(
        modifier = modifier
            .clip(RoundedCornerShape(20))
            .fillMaxWidth(0.7f),
        value = text.value,
        placeholder = {
            Text("Search Here...")
        },
        trailingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        onValueChange = {
            text.value = it
            coroutineScope.launch {
                textFlow.emit(it)
            }
        }
    )
}

@Composable
fun SearchResult(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String
    ) {
    Column(
        modifier = modifier
            .background(Background)
            .fillMaxWidth(0.7f)
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.3f)
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun SheetDesignInput(
    modifier: Modifier = Modifier,
    addressEntity: AddressEntity?,
    saveAction: (AddressEntity) -> Unit,
    dismissAction: () -> Unit
) {
    val address1 = remember { mutableStateOf(addressEntity?.name ?: "") }
    val address2 = remember { mutableStateOf(addressEntity?.subName ?: "") }
    val city = remember { mutableStateOf(addressEntity?.city ?: "") }
    val country = remember { mutableStateOf(addressEntity?.country ?: "") }
    val zip = remember { mutableStateOf(addressEntity?.zip ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    fontWeight = FontWeight.Bold,
                    text = "Street",
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = address1.value,
                    onValueChange = {
                        address1.value = it
                    },
                    modifier = modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter Your Street here!",
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Column {
                Text(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    fontWeight = FontWeight.Bold,
                    text = "Area",
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = address2.value,
                    onValueChange = {
                        address2.value = it
                    },
                    modifier = modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter Your Area here!",
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Column {
                Text(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    fontWeight = FontWeight.Bold,
                    text = "City",
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = city.value,
                    onValueChange = {
                    },
                    modifier = modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter Your City here!",
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        item {
            Column {
                Text(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    fontWeight = FontWeight.Bold,
                    text = "Country",
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    value = country.value,
                    onValueChange = {
                    },
                    modifier = modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter Your Street here!",
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        item {
            Column {
                Text(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    fontWeight = FontWeight.Bold,
                    text = "ZIP",
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    value = zip.value,
                    onValueChange = {
                    },
                    modifier = modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter Your ZIP here!",
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val data = AddressEntity(
                            name = address1.value,
                            id = "",
                            subName = address2.value,
                            country = country.value,
                            city = city.value,
                            zip = zip.value,
                            latitude = addressEntity?.latitude ?: 0.0,
                            longitude = addressEntity?.latitude ?: 0.0,
                        )
                        saveAction(data)
                        dismissAction()

                    },
                    enabled =( address1.value.isNotBlank() && address2.value.isNotBlank() && zip.value.isNotBlank() && city.value.isNotBlank() && country.value.isNotBlank()),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Primary,
                        contentColor = Background,
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .fillMaxWidth(0.5f)
                ) {
                    Text("Save")
                }
                OutlinedButton (
                    onClick = {
                        dismissAction()
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

@Preview
@Composable
private fun PreviewMapPage() {
}