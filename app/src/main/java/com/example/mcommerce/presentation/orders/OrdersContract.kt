package com.example.mcommerce.presentation.orders

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.OrderEntity

interface OrdersContract {
    interface OrdersViewModel {
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action {
        data class ClickOnOrder(val order: OrderEntity) : Action
    }

    sealed interface States {
        data object Loading : States
        data class Success(val ordersList: List<OrderEntity>) : States
        data class Failure(val errorMessage: String) : States
        data object Idle : States
    }

    sealed interface Events {
        data class NavigateToOrderDetails(val order: String) : Events
        data class ShowCurrency(val currency: String, val rate: Double) : Events
        data object Idle : Events
    }
}