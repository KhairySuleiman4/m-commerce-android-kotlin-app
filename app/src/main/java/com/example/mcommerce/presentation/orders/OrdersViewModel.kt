package com.example.mcommerce.presentation.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.domain.usecases.GetOrdersUseCase
import com.example.mcommerce.domain.usecases.GetUserAccessTokenUseCase
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val ordersUseCase: GetOrdersUseCase,
    private val getUserAccessTokenUseCase: GetUserAccessTokenUseCase,
    private val getCurrentCurrencyUseCase: GetCurrentCurrencyUseCase,
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase
): ViewModel(), OrdersContract.OrdersViewModel{

    private val _states = mutableStateOf<OrdersContract.States>(OrdersContract.States.Idle)
    private val _events = mutableStateOf<OrdersContract.Events>(OrdersContract.Events.Idle)

    override val states: State<OrdersContract.States> get() = _states
    override val events: State<OrdersContract.Events> get() = _events

    private var userAccessToken = ""

    override fun invokeActions(action: OrdersContract.Action) {
        when(action){
            is OrdersContract.Action.ClickOnOrder -> {
                val order = action.order
                val jsonString = Json.encodeToString(order)
                _events.value = OrdersContract.Events.NavigateToOrderDetails(jsonString)
            }
        }
    }

    fun getOrders(){
        viewModelScope.launch {
            if (userAccessToken.isBlank()) {
                val accessToken = getUserAccessTokenUseCase()
                val json = Gson().fromJson(accessToken, JsonObject::class.java)
                userAccessToken = json.get("token")?.asString ?: ""
            }
            ordersUseCase(userAccessToken).collect{
                when(it){
                    is ApiResult.Failure -> {
                        _states.value = OrdersContract.States.Failure(it.error.message.toString())
                    }
                    is ApiResult.Loading -> {
                        _states.value = OrdersContract.States.Loading
                    }
                    is ApiResult.Success -> {
                        _states.value = OrdersContract.States.Success(it.data)
                    }
                }
            }
        }
    }

    fun getCurrency(){
        viewModelScope.launch {
            _events.value = OrdersContract.Events.ShowCurrency(
                getCurrentCurrencyUseCase(),
                getCurrentExchangeRateUseCase()
            )
        }
    }

    fun resetEvent() {
        _events.value = OrdersContract.Events.Idle
    }

}