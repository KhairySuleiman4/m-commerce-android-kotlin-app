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
    private val productsUseCase: GetProductsUseCase,
): ViewModel(), ProductsContract.ProductsViewModel {

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
                                productType = it.productType
                            )
                        }
                        _states.value = ProductsContract.States.Success(products)
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

    fun resetEvent() {
        _events.value = ProductsContract.Events.Idle
    }
}