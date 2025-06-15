package com.example.mcommerce.presentation.map.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.presentation.map.MapContract
import com.example.mcommerce.presentation.map.viewmodel.MapViewModel
import com.example.mcommerce.presentation.theme.Background
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

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    ) {
    val address = remember { mutableStateOf<AddressEntity?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(address.value?.latitude ?: 2.0, address.value?.longitude ?: 3.0), 10f)
    }
    LaunchedEffect(Unit) {
        viewModel.getSelectedLocation(30.1,32.3)
    }

    val event = viewModel.events.value
    LaunchedEffect(event) {
        when(event){
            is MapContract.Events.ChangedAddress -> {
                address.value = event.address
                cameraPositionState.position =  CameraPosition.fromLatLngZoom(LatLng(address.value?.latitude ?: 2.0, address.value?.longitude ?: 3.0), 10f)
            }
            MapContract.Events.Idle -> {

            }
            is MapContract.Events.ShowError -> {

            }
        }
    }
    MapPage(
        modifier = modifier,
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

}

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    state: MapContract.States,
    address: LatLng,
    cameraPositionState: CameraPositionState,
    searchAction: (String)-> Unit,
    selectMapAction: (Double, Double)-> Unit,
    selectAction: (AddressEntity)-> Unit
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

    LazyColumn(
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
                                selectAction(state.addressList[it])
                                text.value=""
                            },
                        title = state.addressList[it].name,
                        subtitle = state.addressList[it].country
                    )
                }
            }
        }

    }
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

@Preview
@Composable
private fun PreviewMapPage() {
    MapScreen()
}