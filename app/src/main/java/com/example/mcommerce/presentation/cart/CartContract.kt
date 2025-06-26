package com.example.mcommerce.presentation.cart

import androidx.activity.ComponentActivity
import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.CartEntity

interface CartContract {

    interface CartViewModel {
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action {
        data class ClickOnPlusItem(val variantId: String, val quantity: Int) : Action
        data class ClickOnMinusItem(val variantId: String, val quantity: Int) : Action
        data class ClickOnRemoveItem(val variantId: String) : Action
        data class ClickOnApplyDiscount(val code: String) : Action
        data class ClickOnSubmit(val activity: ComponentActivity, val isCredit: Boolean) : Action
    }

    sealed interface Events {
        data object Idle : Events
        data object DisableApplyEvent : Events
        data class DisplayError(val msg: String) : Events
        data class SetCurrency(val currency: String, val rate: Double) : Events
    }

    sealed interface States {
        data object Loading : States
        data class Success(val cart: CartEntity) : States
        data class Failure(val errorMsg: String) : States
        data object Idle : States
    }
}