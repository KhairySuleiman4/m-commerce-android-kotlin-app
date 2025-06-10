package com.example.mcommerce.presentation.products

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsUseCase: GetProductsUseCase
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
                                price = it.price
                            )
                        }
                        _states.value = ProductsContract.States.Success(products)
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
                toggleCart(action.productId)
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

    private fun toggleCart(productId: String){
        //add product to cart
        val currentState = _states.value
        if(currentState is ProductsContract.States.Success){
            val updatedList = currentState.productsList.map {
                if(it.id == productId) it.copy(isInCart = !it.isInCart)
                else it
            }
            _states.value = currentState.copy(productsList = updatedList)
            val message = if (updatedList.find { it.id == productId}?.isInCart == true)
                "Added to cart!" else "Removed from Cart."
            _events.value = ProductsContract.Events.ShowSnackbar(message)
        }
    }

    fun resetEvent() {
        _events.value = ProductsContract.Events.Idle
    }

}