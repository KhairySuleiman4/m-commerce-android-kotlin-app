package com.example.mcommerce.presentation.map.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.usecases.AddAddressUseCase
import com.example.mcommerce.domain.usecases.GetAddressesUseCase
import com.example.mcommerce.domain.usecases.GetUserAccessTokenUseCase
import com.example.mcommerce.domain.usecases.GetUserNameUseCase
import com.example.mcommerce.presentation.map.MapContract
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val useCase: GetAddressesUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val getUserAccessTokenUseCase: GetUserAccessTokenUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel(), MapContract.MapViewModel {
    private val _states = mutableStateOf<MapContract.States>(MapContract.States.Idle)
    private val _events = mutableStateOf<MapContract.Events>(MapContract.Events.Idle)

    override val states: State<MapContract.States> get() = _states
    override val events: State<MapContract.Events> get() = _events

    override fun invokeActions(action: MapContract.Action) {
        when (action) {
            is MapContract.Action.ClickOnMapLocation -> {
                getSelectedLocation(action.latitude, action.longitude)
            }

            is MapContract.Action.ClickOnResult -> {
                getSearchLocation(placeId = action.addressId)
            }

            is MapContract.Action.SearchPlace -> {
                getSearchResult(action.place)
            }

            is MapContract.Action.ClickOnSave -> {
                addAddress(action.address)
            }
        }
    }

    private fun getSearchResult(place: String) {
        viewModelScope.launch {
            useCase(place).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _states.value = MapContract.States.Failure(it.error.message.toString())
                    }

                    is ApiResult.Loading -> {
                        _states.value = MapContract.States.Loading
                    }

                    is ApiResult.Success -> {
                        _states.value = MapContract.States.Success(it.data ?: listOf())
                    }
                }
            }
        }
    }

    private fun getSearchLocation(placeId: String) {
        viewModelScope.launch {
            useCase.getAddressByPlaceId(placeId).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _events.value = MapContract.Events.ShowError(it.error.message.toString())
                    }

                    is ApiResult.Loading -> {
                    }

                    is ApiResult.Success -> {
                        if (it.data != null) {
                            _events.value = MapContract.Events.ChangedAddress(it.data)
                            _states.value = MapContract.States.Idle
                        } else {
                            _events.value = MapContract.Events.ShowError("Couldn't get the address")
                            _states.value = MapContract.States.Idle
                        }
                    }
                }
            }
        }
    }

    private fun addAddress(address: AddressEntity) {
        viewModelScope.launch {
            val accessToken = getUserAccessTokenUseCase()
            val json = Gson().fromJson(accessToken, JsonObject::class.java)
            val access = json.get("token")?.asString
            addAddressUseCase(access ?: "", address, getUserNameUseCase()).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _events.value = MapContract.Events.ShowError(it.error.message.toString())

                    }

                    is ApiResult.Loading -> {
                        _events.value = MapContract.Events.ShowError("Saving...")

                    }

                    is ApiResult.Success -> {
                        if (it.data) {
                            _events.value = MapContract.Events.ShowError("Saved Successfully")
                            delay(1500)
                            _events.value = MapContract.Events.SavedAddress
                        } else {
                            _events.value =
                                MapContract.Events.ShowError("The Address couldn't be saved")
                        }
                    }
                }
            }
        }
    }

    fun getSelectedLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            useCase(latitude, longitude).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _events.value = MapContract.Events.ShowError(it.error.message.toString())
                    }

                    is ApiResult.Loading -> {

                    }

                    is ApiResult.Success -> {
                        _events.value =
                            it.data?.let { it1 -> MapContract.Events.ChangedAddress(it1) }
                                ?: MapContract.Events.ShowError("No Search Result")
                        _states.value = MapContract.States.Idle
                    }
                }
            }
        }
    }

}