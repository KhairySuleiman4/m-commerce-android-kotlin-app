package com.example.mcommerce.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetAllProductsUseCase
import com.example.mcommerce.domain.usecases.GetBrandsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productsUseCase: GetAllProductsUseCase,
    private val brandsUseCase: GetBrandsUseCase
): ViewModel(), SearchContract.SearchViewModel {

    private val _state = MutableStateFlow(SearchContract.ProductState())
    val state: StateFlow<SearchContract.ProductState> = _state

    init {
        getAllProductsAndBrands()
    }

    private fun getAllProductsAndBrands(){
        viewModelScope.launch {
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