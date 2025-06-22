package com.example.mcommerce.presentation.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.usecases.DeleteFavoriteProductUseCase
import com.example.mcommerce.domain.usecases.GetAllProductsUseCase
import com.example.mcommerce.domain.usecases.GetBrandsUseCase
import com.example.mcommerce.domain.usecases.GetFavoriteProductsUseCase
import com.example.mcommerce.domain.usecases.InsertProductToFavoritesUseCase
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productsUseCase: GetAllProductsUseCase,
    private val brandsUseCase: GetBrandsUseCase,
    private val getCurrencyUseCase: GetCurrentCurrencyUseCase,
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val insertProductToFavoritesUseCase: InsertProductToFavoritesUseCase,
    private val deleteFavoriteProductUseCase: DeleteFavoriteProductUseCase
): ViewModel(), SearchContract.SearchViewModel {

    private val _state = MutableStateFlow(SearchContract.ProductState())
    val state: StateFlow<SearchContract.ProductState> = _state

    private val _events = mutableStateOf<SearchContract.Events>(SearchContract.Events.Idle)
    override val events: State<SearchContract.Events> get() = _events

    fun getAllProductsAndBrands(){
        viewModelScope.launch(Dispatchers.IO) {
            productsUseCase().collect{ products ->
                when(products){
                    is ApiResult.Failure -> {
                        _state.update {
                            it.copy(
                                errorMsg = products.error.message,
                                isLoading = false
                            )
                        }
                    }
                    is ApiResult.Loading -> {
                        _state.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is ApiResult.Success -> {
                        _state.update {
                            it.copy(
                                allProducts = products.data,
                                isLoading = false
                            )
                        }
                        getFavorites()
                        brandsUseCase().collect{ brands ->
                            when(brands){
                                is ApiResult.Failure -> {
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            errorMsg = brands.error.message
                                        )
                                    }
                                }
                                is ApiResult.Loading -> {
                                    _state.update {
                                        it.copy(isLoading = true)
                                    }
                                }
                                is ApiResult.Success -> {
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            brands = brands.data.map { brand ->
                                                brand.title
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getCurrency(){
        viewModelScope.launch {
            val currency = getCurrencyUseCase()
            val rate = getCurrentExchangeRateUseCase()
            _events.value = SearchContract.Events.ShowCurrency(currency, rate)
        }
    }

    private fun getFavorites(){
        viewModelScope.launch(Dispatchers.IO) {
            getFavoriteProductsUseCase().collect{ result ->
                when(result){
                    is ApiResult.Failure -> {
                        _state.update {
                            it.copy(
                                errorMsg = result.error.message,
                                isLoading = false
                            )
                        }
                    }
                    is ApiResult.Loading -> {
                        _state.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is ApiResult.Success -> {
                        val newData = _state.value.allProducts.map { product ->
                            if(result.data.any { it.id ==  product.id }){
                                product.copy(isFavorite = true)
                            } else{
                                product
                            }
                        }
                        _state.update {
                            it.copy(
                                allProducts = newData,
                                isLoading = false
                            )
                        }
                        applyFilter()
                    }
                }
            }
        }
    }

    override fun invokeActions(action: SearchContract.Action) {
        when(action){
            is SearchContract.Action.OnSearchQueryChanged -> {
                _state.update {
                    it.copy(
                        filter = it.filter.copy(searchQuery = action.query)
                    )
                }
                applyFilter()
            }
            is SearchContract.Action.OnBrandSelected -> {
                _state.update {
                    it.copy(
                        filter = it.filter.copy(brand = action.brand)
                    )
                }
                applyFilter()
            }
            is SearchContract.Action.OnTypeSelected -> {
                _state.update {
                    it.copy(
                        filter = it.filter.copy(type = action.type)
                    )
                }
                applyFilter()
            }
            is SearchContract.Action.OnPriceRangeChanged -> {
                _state.update {
                    it.copy(
                        filter = it.filter.copy(
                            minPrice = action.min,
                            maxPrice = action.max
                        )
                    )
                }
                applyFilter()
            }

            is SearchContract.Action.ClickOnFavoriteIcon -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if(action.product.isFavorite){
                        insertProductToFavoritesUseCase(action.product)
                        updateFavoriteState(action.product)
                    } else{
                        deleteFavoriteProductUseCase(action.product.id)
                        updateFavoriteState(action.product)
                    }
                }
            }
        }
    }

    private fun updateFavoriteState(product: ProductSearchEntity){
        val currentState = _state.value
        val updatedAllProducts = currentState.allProducts.map {
            if (it.id == product.id) {
                it.copy(isFavorite = !it.isFavorite)
            }
            else {
                it
            }
        }
        val updatedFilteredProducts = currentState.filteredProducts.map {
            if (it.id == product.id) {
                it.copy(isFavorite = !it.isFavorite)
            }
            else {
                it
            }
        }
        _state.update {
            it.copy(
                allProducts = updatedAllProducts,
                filteredProducts = updatedFilteredProducts
            )
        }
    }

    private fun applyFilter(){
        val (type, brand, min, max, query)  = _state.value.filter

        if (query.isBlank()) {
            _state.update { it.copy(filteredProducts = emptyList()) }
            return
        }

        val filtered = _state.value.allProducts.filter{ product ->
            (type == null || product.productType.equals(type, ignoreCase = true)) &&
            (brand == null || product.brand.equals(brand, ignoreCase = true)) &&
            (min == null || product.price >= min) &&
            (max == null || product.price <= max) &&
            product.title.contains(query, ignoreCase = true)
        }

        _state.update {
            it.copy(filteredProducts = filtered)
        }
    }

}