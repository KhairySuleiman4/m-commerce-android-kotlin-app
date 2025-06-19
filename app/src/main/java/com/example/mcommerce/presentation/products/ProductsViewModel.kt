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
) : ViewModel(), ProductsContract.ProductsViewModel {

    private val _states = mutableStateOf<ProductsContract.States>(ProductsContract.States.Idle)
    private val _events = mutableStateOf<ProductsContract.Events>(ProductsContract.Events.Idle)

    override val states: State<ProductsContract.States> get() = _states
    override val events: State<ProductsContract.Events> get() = _events

    private var allProducts: List<ProductsContract.ProductUIModel> = emptyList()
    private var currentCollectionId: String = ""

    fun getProducts(id: String) {
        viewModelScope.launch {
            currentCollectionId = id
            productsUseCase(id).collect { result ->
                when (result) {
                    is ApiResult.Failure -> {
                        _states.value =
                            ProductsContract.States.Failure(result.error.message.toString())
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
                                variantId = it.variantId,
                                productType = it.productType
                            )
                        }
                        _states.value = ProductsContract.States.Success(products)
                        getCart()
                        allProducts = products
                        updateSuccessState()
                    }
                }
            }
        }
    }

    override fun invokeActions(action: ProductsContract.Action) {
        when (action) {
            is ProductsContract.Action.ClickOnProduct -> {
                _events.value = ProductsContract.Events.NavigateToProductDetails(action.productId)
            }

            is ProductsContract.Action.ClickOnAddToCart -> {
                toggleCart(action.variantId)
            }

            is ProductsContract.Action.OnTypeSelected -> {
                filterProductsByType(action.productType)
            }

            is ProductsContract.Action.ClickOnFavorite -> {
                toggleFavorite(action.productId)
            }
        }
    }

    private fun filterProductsByType(selectedProductType: String?) {
        val currentState = _states.value
        if (currentState is ProductsContract.States.Success) {
            val filteredProducts = if (selectedProductType == null) {
                allProducts
            } else {
                allProducts.filter { it.productType.equals(selectedProductType, ignoreCase = true) }
            }

            _states.value = currentState.copy(
                filteredProductsList = filteredProducts,
                selectedProductType = selectedProductType
            )
        }
    }

    private fun updateSuccessState() {
        _states.value = ProductsContract.States.Success(
            productsList = allProducts,
            filteredProductsList = allProducts,
            selectedProductType = null,
        )
    }

    private fun toggleFavorite(productId: String) {
        //add product to wishlist
        val currentState = _states.value
        if (currentState is ProductsContract.States.Success) {
            val updatedAllProducts = currentState.productsList.map {
                if (it.id == productId) it.copy(isFavorite = !it.isFavorite)
                else it
            }
            val updatedFilteredProducts = currentState.filteredProductsList.map {
                if (it.id == productId) it.copy(isFavorite = !it.isFavorite)
                else it
            }
            allProducts = updatedAllProducts
            _states.value = currentState.copy(
                productsList = updatedAllProducts,
                filteredProductsList = updatedFilteredProducts
            )
            val message =
                if (updatedFilteredProducts.find { it.id == productId }?.isFavorite == true)
                    "Added to Favorites!" else "Removed from Favorites."
            _events.value = ProductsContract.Events.ShowSnackbar(message)
        }
    }

    private fun getCart() {
        viewModelScope.launch {
            getCartUseCase().collect { result ->
                when (result) {
                    is ApiResult.Failure -> {   }
                    is ApiResult.Loading -> {   }
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
                                allProducts = newData
                                val filteredProducts = (_states.value as ProductsContract.States.Success).filteredProductsList
                                val newFilteredProducts = filteredProducts.map { item ->
                                    if (result.data.items.any { it.id == item.variantId })
                                        item.copy(isInCart = true)
                                    else
                                        item
                                }
                                _states.value = ProductsContract.States.Success(
                                    productsList = newData,
                                    filteredProductsList = newFilteredProducts
                                    )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleCart(variantId: String) {
        viewModelScope.launch {
            addItemToCartUseCase(variantId, 1).collect { result ->
                when (result) {
                    is ApiResult.Failure -> {
                        _events.value = ProductsContract.Events.ShowSnackbar(
                            result.error.message ?: "Unknown Error"
                        )
                    }

                    is ApiResult.Loading -> {

                    }

                    is ApiResult.Success -> {
                        if (result.data != null) {
                            val currentState = _states.value
                            if (currentState is ProductsContract.States.Success) {
                                val updatedList = currentState.productsList.map { item ->
                                    if (result.data.items.any { it.id == item.variantId }) item.copy(
                                        isInCart = true
                                    )
                                    else item
                                }
                                val filteredProducts = (_states.value as ProductsContract.States.Success).filteredProductsList
                                val newFilteredProducts = filteredProducts.map { item ->
                                    if (result.data.items.any { it.id == item.variantId })
                                        item.copy(isInCart = true)
                                    else
                                        item
                                }
                                _states.value = ProductsContract.States.Success(
                                    productsList = updatedList,
                                    filteredProductsList = newFilteredProducts
                                )
                                allProducts = updatedList
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