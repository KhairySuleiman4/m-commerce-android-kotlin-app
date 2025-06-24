package com.example.mcommerce.presentation.cart.viewmodel

import androidx.activity.ComponentActivity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.AddDiscountToCartUseCase
import com.example.mcommerce.domain.usecases.ChangeCartItemInCartUseCase
import com.example.mcommerce.domain.usecases.CheckForDefaultAddressUseCase
import com.example.mcommerce.domain.usecases.ClearOnlineCartUseCase
import com.example.mcommerce.domain.usecases.GetCartUseCase
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.domain.usecases.GetUserAccessTokenUseCase
import com.example.mcommerce.domain.usecases.RemoveItemFromCartUseCase
import com.example.mcommerce.presentation.cart.CartContract
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val changeQuantityUseCase: ChangeCartItemInCartUseCase,
    private val addDiscountToCartUseCase: AddDiscountToCartUseCase,
    private val removeItemFromCartUseCase: RemoveItemFromCartUseCase,
    private val getCurrentCurrencyUseCase: GetCurrentCurrencyUseCase,
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase,
    private val checkForDefaultAddressUseCase: CheckForDefaultAddressUseCase,
    private val getUserAccessTokenUseCase: GetUserAccessTokenUseCase,
    private val clearOnlineCartUseCase: ClearOnlineCartUseCase
) : ViewModel(), CartContract.CartViewModel {

    private val _states = mutableStateOf<CartContract.States>(CartContract.States.Idle)
    private val _events = mutableStateOf<CartContract.Events>(CartContract.Events.Idle)

    override val states: State<CartContract.States> get() = _states
    override val events: State<CartContract.Events> get() = _events

    private var canCheckout: Boolean? = null
    private var eventProcessor: DefaultCheckoutEventProcessor? = null

    override fun invokeActions(action: CartContract.Action) {
        when (action) {
            is CartContract.Action.ClickOnApplyDiscount -> {
                addDiscount(action.code)
            }

            is CartContract.Action.ClickOnMinusItem -> {
                changeQuantity(action.variantId, action.quantity - 1)
            }

            is CartContract.Action.ClickOnPlusItem -> {
                changeQuantity(action.variantId, action.quantity + 1)
            }

            is CartContract.Action.ClickOnRemoveItem -> {
                removeItem(action.variantId)
            }

            is CartContract.Action.ClickOnSubmit -> {
                checkDefault(action.activity)
            }
        }
    }

    fun setEventProcessor(activity: ComponentActivity, action: () -> Unit) {
        eventProcessor = object : DefaultCheckoutEventProcessor(activity) {
            override fun onCheckoutCanceled() {

            }

            override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {
                checkoutCompleted(action)
            }

            override fun onCheckoutFailed(error: CheckoutException) {
                checkoutFailed(error)
            }
        }
    }

    private fun checkoutFailed(error: CheckoutException) {
        _events.value =
            CartContract.Events.DisplayError(error.message ?: "There is something wrong")
    }

    private fun checkoutCompleted(action: () -> Unit) {
        viewModelScope.launch {
            clearOnlineCartUseCase().collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _events.value = CartContract.Events.DisplayError(
                            it.error.message ?: "There is something wrong"
                        )
                    }

                    is ApiResult.Loading -> {

                    }

                    is ApiResult.Success -> {
                        _events.value = CartContract.Events.DisplayError("Success Transaction")
                        delay(1000)
                        action()
                    }
                }
            }
        }
    }

    fun checkout(activity: ComponentActivity) {
        viewModelScope.launch {
            val processor = eventProcessor
            if (_states.value is CartContract.States.Success && processor != null) {
                val cart = (_states.value as CartContract.States.Success).cart
                withContext(Dispatchers.Main) {
                    ShopifyCheckoutSheetKit.present(
                        cart.checkout,
                        activity,
                        processor
                    )
                }
            }
        }
    }

    fun getCurrency() {
        viewModelScope.launch {
            val currency = getCurrentCurrencyUseCase()
            val rate = getCurrentExchangeRateUseCase()
            _events.value = CartContract.Events.SetCurrency(currency, rate)
        }
    }

    fun getCart() {
        viewModelScope.launch {
            getCartUseCase().collect { result ->
                when (result) {
                    is ApiResult.Failure -> {
                        _states.value = CartContract.States.Failure(
                            result.error.message ?: "Not able to get the cart"
                        )
                    }

                    is ApiResult.Loading -> {
                        _states.value = CartContract.States.Loading
                    }

                    is ApiResult.Success -> {
                        if (result.data != null) {
                            _states.value = CartContract.States.Success(result.data)
                            if (result.data.discountAmount > 0)
                                _events.value = CartContract.Events.DisableApplyEvent
                        } else
                            _states.value = CartContract.States.Failure("Not able to get the cart")
                    }
                }
            }
        }
    }

    private fun checkDefault(activity: ComponentActivity) {
        if (canCheckout != null) {
            if (canCheckout as Boolean) {
                checkout(activity)
            } else {
                _events.value =
                    CartContract.Events.DisplayError("Sorry you don't have an address to deliver to")
            }
        } else {
            viewModelScope.launch {
                val accessToken = getUserAccessTokenUseCase()
                val json = Gson().fromJson(accessToken, JsonObject::class.java)
                val token = json.get("token")?.asString ?: ""
                checkForDefaultAddressUseCase(token).collect {
                    when (it) {
                        is ApiResult.Failure -> {
                            _events.value = CartContract.Events.DisplayError(
                                it.error.message ?: "Sorry there is something wrong"
                            )
                        }

                        is ApiResult.Loading -> {

                        }

                        is ApiResult.Success -> {
                            canCheckout = it.data
                            if (canCheckout as Boolean) {
                                checkout(activity)
                            } else {
                                _events.value =
                                    CartContract.Events.DisplayError("Sorry you don't have an address to deliver to")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeQuantity(variantId: String, quantity: Int) {
        viewModelScope.launch {
            changeQuantityUseCase(variantId, quantity).collect { result ->
                when (result) {
                    is ApiResult.Failure -> {
                        _events.value = CartContract.Events.DisplayError(
                            result.error.message ?: "Couldn't update the item"
                        )
                    }

                    is ApiResult.Loading -> {
                    }

                    is ApiResult.Success -> {
                        if (result.data != null) {
                            if (_states.value is CartContract.States.Success && result.data.items.toSet() == (_states.value as CartContract.States.Success).cart.items.toSet()) {
                                _events.value =
                                    CartContract.Events.DisplayError("you have reached the maximum quantity")
                            }
                            _states.value = CartContract.States.Success(result.data)

                        } else
                            _states.value = CartContract.States.Failure("There is something wrong")
                    }
                }
            }
        }
    }

    private fun addDiscount(code: String) {
        viewModelScope.launch {
            addDiscountToCartUseCase(code).collect { result ->
                when (result) {
                    is ApiResult.Failure -> {
                        _events.value = CartContract.Events.DisplayError(
                            result.error.message ?: "Couldn't apply the code"
                        )
                    }

                    is ApiResult.Loading -> {
                    }

                    is ApiResult.Success -> {
                        if (result.data != null) {
                            _states.value = CartContract.States.Success(result.data)
                            if (result.data.discountAmount > 0) {
                                _events.value = CartContract.Events.DisableApplyEvent
                                _events.value =
                                    CartContract.Events.DisplayError("The Code was Applied Successfully")
                            } else {
                                _events.value =
                                    CartContract.Events.DisplayError("The Code couldn't be Applied")
                            }
                        } else
                            _states.value = CartContract.States.Failure("There is something wrong")
                    }
                }
            }
        }
    }

    private fun removeItem(itemId: String) {
        viewModelScope.launch {
            removeItemFromCartUseCase(itemId).collect { result ->
                when (result) {
                    is ApiResult.Failure -> {
                        _events.value = CartContract.Events.DisplayError(
                            result.error.message ?: "Couldn't Remove the item"
                        )
                    }

                    is ApiResult.Loading -> {
                    }

                    is ApiResult.Success -> {
                        if (result.data != null)
                            _states.value = CartContract.States.Success(result.data)
                        else
                            _states.value = CartContract.States.Failure("There is something wrong")
                    }
                }
            }
        }
    }

    fun resetEvents() {
        _events.value = CartContract.Events.Idle
    }
}