package com.example.mcommerce.presentation.order_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class OrderDetailsViewModel @Inject constructor(): ViewModel(), OrderDetailsContract.OrderDetailsViewModel {

    private val _events = mutableStateOf<OrderDetailsContract.Events>(OrderDetailsContract.Events.Idle)

    override val events: State<OrderDetailsContract.Events> get() = _events

    override fun invokeActions(action: OrderDetailsContract.Action) {
        when(action){
            is OrderDetailsContract.Action.ClickOnItem -> {
                _events.value = OrderDetailsContract.Events.NavigateToProductInfo(action.productId)
            }
        }
    }

    fun resetEvent() {
        _events.value = OrderDetailsContract.Events.Idle
    }

}