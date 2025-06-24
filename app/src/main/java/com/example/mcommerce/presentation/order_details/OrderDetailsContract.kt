package com.example.mcommerce.presentation.order_details

import androidx.compose.runtime.State

interface OrderDetailsContract {
    interface OrderDetailsViewModel{
        fun invokeActions(action: Action)
        val events: State<Events>
    }

    sealed interface Action{
        data class ClickOnItem(val productId: String): Action
    }


    sealed interface Events{
        data class NavigateToProductInfo(val productId: String): Events
        data object Idle: Events
    }
}