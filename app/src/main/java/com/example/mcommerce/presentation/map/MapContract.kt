package com.example.mcommerce.presentation.map

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.AddressEntity

interface MapContract {
    interface  MapViewModel{
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action{
        data class SearchPlace(val place: String): Action
        data class ClickOnResult(val addressEntity: AddressEntity): Action
        data class ClickOnMapLocation(val latitude: Double, val longitude: Double): Action
    }

    sealed interface States{
        data object Loading: States
        data class Success(val addressList: List<AddressEntity>): States
        data class Failure(val errorMessage: String): States
        data object Idle: States
    }

    sealed interface Events{
        data class ChangedAddress(val address: AddressEntity): Events
        data class ShowError(val errorMessage: String): Events
        data object Idle: Events
    }
}