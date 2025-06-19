package com.example.mcommerce.presentation.products

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.AddItemToCartUseCase
import com.example.mcommerce.domain.usecases.GetCartUseCase
import com.example.mcommerce.domain.usecases.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsUseCase: GetProductsUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val addItemToCartUseCase: AddItemToCartUseCase,
): ViewModel(), ProductsContract.ProductsViewModel {

    private val _states = mutableStateOf<ProductsContract.States>(ProductsContract.States.Idle)
    private val _events = mutableStateOf<ProductsContract.Events>(ProductsContract.Events.Idle)

    override val states: State<ProductsContract.States> get() = _states
    override val events: State<ProductsContract.Events> get() = _events

    fun getProducts(id: String){
        viewModelScope.launch {
            productsUseCase(id).collect{ result ->
                when(result){
                    is ApiResult.Failure -> {
                        _states.value = ProductsContract.States.Failure(result.error.message.toString())
                    }
                    is ApiResult.Loading -> {
                        _states.value = ProductsContract.States.Loading
                    }
                    is ApiResult.Success -> {
                        val products = result.data.map {
                            ProductsContract.ProductUIModel(
                                id = it.id,
                                title = it.title,
                                imageUrl = it.imageUrl,
                                price = it.price,
                                variantId = it.variantId
                            )
                        }
                        _states.value = ProductsContract.States.Success(products)
                        getCart()
                    }
                }
            }
        }
    }

    override fun invokeActions(action: ProductsContract.Action) {
        when(action){
            is ProductsContract.Action.ClickOnProduct -> {
                _events.value = ProductsContract.Events.NavigateToProductDetails(action.productId)
            }
            is ProductsContract.Action.ClickOnAddToCart -> {
                toggleCart(action.variantId)
            }
            is ProductsContract.Action.ClickOnFavorite -> {
                toggleFavorite(action.productId)
            }
        }
    }

    private fun toggleFavorite(productId: String){
        //add product to wishlist
        val currentState = _states.value
        if (currentState is ProductsContract.States.Success) {
            val updatedList = currentState.productsList.map {
                if (it.id == productId) it.copy(isFavorite = !it.isFavorite)
                else it
            }
            _states.value = currentState.copy(productsList = updatedList)
            val message = if (updatedList.find { it.id == productId }?.isFavorite == true)
                "Added to Favorites!" else "Removed from Favorites."
            _events.value = ProductsContract.Events.ShowSnackbar(message)
        }
    }

    private fun getCart() {
        viewModelScope.launch {
            getCartUseCase().collect { result ->
                when (result) {
                    is ApiResult.Failure -> {

                    }

                    is ApiResult.Loading -> {

                    }

                    is ApiResult.Success -> {
                        if (result.data != null) {
                            if (_states.value is ProductsContract.States.Success) {
                                val data =
                                    (_states.value as ProductsContract.States.Success).productsList
                                val newData = data.map { item ->
                                    if (result.data.items.any { it.id == item.variantId })
                                        item.copy(isInCart = true)
                                    else
                                        item
                                }
                                _states.value = ProductsContract.States.Success(newData)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleCart(variantId: String){
        viewModelScope.launch {
            addItemToCartUseCase(variantId, 1).collect{ result ->
                when(result){
                    is ApiResult.Failure -> {
                        _events.value = ProductsContract.Events.ShowSnackbar(result.error.message ?: "Unknown Error")
                    }
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        if (result.data != null) {
                            val currentState = _states.value
                            if (currentState is ProductsContract.States.Success) {
                                val updatedList = currentState.productsList.map { item ->
                                    if (result.data.items.any { it.id == item.variantId }) item.copy(isInCart = true)
                                    else item
                                }
                                Log.i("cart", result.data.id)
                                _states.value = currentState.copy(productsList = updatedList)
                                val message =
                                    if (updatedList.find { it.variantId == variantId }?.isInCart == true)
                                        "Added to cart!" else "Removed from Cart."
                                _events.value = ProductsContract.Events.ShowSnackbar(message)
                            }
                        }
                    }
                }
            }
        }
    }

    fun resetEvent() {
        _events.value = ProductsContract.Events.Idle
    }

}