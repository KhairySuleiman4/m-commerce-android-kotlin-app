package com.example.mcommerce.presentation.order_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val getCurrentCurrencyUseCase: GetCurrentCurrencyUseCase,
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase
) : ViewModel(), OrderDetailsContract.OrderDetailsViewModel {

    private val _events =
        mutableStateOf<OrderDetailsContract.Events>(OrderDetailsContract.Events.Idle)

    override val events: State<OrderDetailsContract.Events> get() = _events

    override fun invokeActions(action: OrderDetailsContract.Action) {
        when (action) {
            is OrderDetailsContract.Action.ClickOnItem -> {
                _events.value = OrderDetailsContract.Events.NavigateToProductInfo(action.productId)
            }
        }
    }

    fun getCurrency() {
        viewModelScope.launch {
            _events.value = OrderDetailsContract.Events.ShowCurrency(
                getCurrentCurrencyUseCase(),
                getCurrentExchangeRateUseCase()
            )
        }
    }

    fun resetEvent() {
        _events.value = OrderDetailsContract.Events.Idle
    }

}