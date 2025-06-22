package com.example.mcommerce.presentation.products

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.domain.usecases.DeleteFavoriteProductUseCase
import com.example.mcommerce.domain.usecases.GetFavoriteProductsUseCase
import com.example.mcommerce.domain.usecases.GetProductsUseCase
import com.example.mcommerce.domain.usecases.InsertProductToFavoritesUseCase
import com.example.mcommerce.presentation.utils.toSearchEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsUseCase: GetProductsUseCase,
    private val getCurrencyUseCase: GetCurrentCurrencyUseCase,
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val insertProductToFavoritesUseCase: InsertProductToFavoritesUseCase,
    private val deleteFavoriteProductUseCase: DeleteFavoriteProductUseCase
) : ViewModel(), ProductsContract.ProductsViewModel {

    private val _states = mutableStateOf<ProductsContract.States>(ProductsContract.States.Idle)
    private val _events = mutableStateOf<ProductsContract.Events>(ProductsContract.Events.Idle)

    override val states: State<ProductsContract.States> get() = _states
    override val events: State<ProductsContract.Events> get() = _events

    private var allProducts: List<ProductsContract.ProductUIModel> = emptyList()
    private var currentCollectionId: String = ""

    override fun invokeActions(action: ProductsContract.Action) {
        when (action) {
            is ProductsContract.Action.ClickOnProduct -> {
                _events.value = ProductsContract.Events.NavigateToProductDetails(action.productId)
            }
            is ProductsContract.Action.OnTypeSelected -> {
                filterProductsByType(action.productType)
            }

            is ProductsContract.Action.ClickOnFavorite -> {
                toggleFavorite(action.product)
            }
        }
    }

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
                                productType = it.productType,
                                brand = it.brand
                            )
                        }
                        _states.value = ProductsContract.States.Success(products)
                        allProducts = products
                        getFavorites()
                    }
                }
            }
        }
    }

    fun getCurrency(){
        viewModelScope.launch {
            val currency = getCurrencyUseCase()
            val rate = getCurrentExchangeRateUseCase()
            _events.value = ProductsContract.Events.ChangeCurrency(currency, rate)
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

    private fun getFavorites(){
        viewModelScope.launch(Dispatchers.IO) {
            getFavoriteProductsUseCase().collect{ result ->
                when(result){
                    is ApiResult.Failure -> {

                    }
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        if(_states.value is ProductsContract.States.Success) {
                            val data = (_states.value as ProductsContract.States.Success).productsList
                            val newData = data.map { product ->
                                if (result.data.any { it.id == product.id }) {
                                    product.copy(isFavorite = true)
                                } else {
                                    product
                                }
                            }
                            allProducts = newData
                            val newFilteredProducts = newData.map { product ->
                                if (result.data.any { it.id == product.id }) {
                                    product.copy(isFavorite = true)
                                } else {
                                    product
                                }
                            }
                            _states.value = ProductsContract.States.Success(
                                productsList = newData,
                                filteredProductsList = newFilteredProducts,
                                selectedProductType = null
                            )
                        }
                    }
                }
            }
        }
    }

    private fun toggleFavorite(product: ProductsContract.ProductUIModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if(product.isFavorite){
                insertProductToFavoritesUseCase(product.toSearchEntity())
            } else{
                deleteFavoriteProductUseCase(product.id)
            }
        }
        val currentState = _states.value
        if (currentState is ProductsContract.States.Success) {
            val updatedAllProducts = currentState.productsList.map {
                if (it.id == product.id) {
                    it.copy(isFavorite = !it.isFavorite)
                }
                else {
                    it
                }
            }
            val updatedFilteredProducts = currentState.filteredProductsList.map {
                if (it.id == product.id) {
                    it.copy(isFavorite = !it.isFavorite)
                }
                else {
                    it
                }
            }
            allProducts = updatedAllProducts
            _states.value = currentState.copy(
                productsList = updatedAllProducts,
                filteredProductsList = updatedFilteredProducts
            )
            val message =
                if (updatedFilteredProducts.find { it.id == product.id }?.isFavorite == true)
                    "Added to Favorites!" else "Removed from Favorites."
            _events.value = ProductsContract.Events.ShowSnackbar(message)
        }
    }

    fun resetEvent() {
        _events.value = ProductsContract.Events.Idle
    }
}