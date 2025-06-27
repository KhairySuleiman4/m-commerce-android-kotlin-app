package com.example.mcommerce.presentation.addresses

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.AddressEntity

sealed interface AddressesContract {
    interface AddressesViewModel {
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action {
        data class ClickOnSetDefault(val addressId: String) : Action
        data class ClickOnDelete(val addressId: String) : Action
    }

    sealed interface Events {
        data object Idle : Events
        data class ShowError(val msg: String) : Events
    }

    sealed interface States {
        data object Loading : States
        data class Success(val data: List<AddressEntity>) : States
        data class Failure(val errorMsg: String) : States
        data object Idle : States
    }
}