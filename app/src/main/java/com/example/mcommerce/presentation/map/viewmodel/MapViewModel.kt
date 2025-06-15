package com.example.mcommerce.presentation.map.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetAddressesUseCase
import com.example.mcommerce.presentation.map.MapContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val useCase: GetAddressesUseCase
): ViewModel(),MapContract.MapViewModel {
    private val _states = mutableStateOf<MapContract.States>(MapContract.States.Idle)
    private val _events = mutableStateOf<MapContract.Events>(MapContract.Events.Idle)

    override val states: State<MapContract.States> get() = _states
    override val events: State<MapContract.Events> get() = _events

    override fun invokeActions(action: MapContract.Action) {
        when(action){
            is MapContract.Action.ClickOnMapLocation -> {
                getSelectedLocation(action.latitude,action.longitude)
            }
            is MapContract.Action.ClickOnResult -> {
                _events.value = MapContract.Events.ChangedAddress(action.addressEntity)
                _states.value = MapContract.States.Idle
            }
            is MapContract.Action.SearchPlace -> {
                getSearchResult(action.place)
            }
        }
    }

    private fun getSearchResult(place: String){
        viewModelScope.launch {
            useCase(place).collect{
              when(it){
                  is ApiResult.Failure -> {
                      _states.value = MapContract.States.Failure(it.error.message.toString())
                  }
                  is ApiResult.Loading -> {
                      _states.value = MapContract.States.Loading
                  }
                  is ApiResult.Success -> {
                      _states.value = MapContract.States.Success(it.data?: listOf())
                  }
              }
            }
        }
    }

    fun getSelectedLocation(latitude: Double, longitude: Double){
        viewModelScope.launch {
            useCase(latitude, longitude).collect{
                when(it){
                    is ApiResult.Failure -> {
                        _events.value = MapContract.Events.ShowError(it.error.message.toString())
                    }
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        _events.value = it.data?.let { it1 -> MapContract.Events.ChangedAddress(it1) }
                            ?: MapContract.Events.ShowError("No Search Result")
                        _states.value = MapContract.States.Idle
                    }
                }
            }
        }
    }

}