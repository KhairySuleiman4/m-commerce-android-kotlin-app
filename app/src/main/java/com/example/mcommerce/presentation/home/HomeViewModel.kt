package com.example.mcommerce.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetBrandsUseCase
import com.example.mcommerce.domain.usecases.GetBestSellersUseCase
import com.example.mcommerce.domain.usecases.GetLatestArrivalsUseCase
import com.example.mcommerce.type.ProductSortKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val brandsUseCase: GetBrandsUseCase,
    private val homeProductsUseCase: GetBestSellersUseCase,
    private val latestArrivalsUseCase: GetLatestArrivalsUseCase
): ViewModel(), HomeContract.HomeViewModel {

    private val _states = mutableStateOf(HomeContract.HomeState())
    private val _events = mutableStateOf<HomeContract.Events>(HomeContract.Events.Idle)

    override val states: State<HomeContract.HomeState> = _states
    override val events: State<HomeContract.Events> = _events


    override fun invokeActions(action: HomeContract.Action) {
        when(action){
            is HomeContract.Action.LoadHomeData -> {
                loadHomeData()
            }
            is HomeContract.Action.ClickOnBrand -> {
                _events.value = HomeContract.Events.NavigateToBrandProducts(action.brandId, action.brandName)
            }
            is HomeContract.Action.ClickOnProduct -> {
                _events.value = HomeContract.Events.NavigateToProductDetails(action.productId)
            }

            is HomeContract.Action.ClickOnFavorite -> {

            }
        }
    }

    private fun loadHomeData() {
        getBrands()
        getBestSellers()
        getLatestArrivals()
    }

    private fun getBrands() {
        _states.value = _states.value.copy(brandsLoading = true)
        viewModelScope.launch {
            brandsUseCase().collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _states.value = _states.value.copy(brandsLoading = true)
                    }
                    is ApiResult.Success -> {
                        _states.value = _states.value.copy(
                            brandsList = result.data,
                            brandsLoading = false
                        )
                    }
                    is ApiResult.Failure -> {
                        _states.value = _states.value.copy(
                            errorMessage = result.error.message,
                            brandsLoading = false
                        )
                        _events.value = HomeContract.Events.ShowError(result.error.message ?: "An error occurred")
                    }
                }
            }
        }
    }

    private fun getBestSellers() {
        _states.value = _states.value.copy(bestSellersLoading = true)
        viewModelScope.launch {
            homeProductsUseCase(ProductSortKeys.BEST_SELLING, false).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _states.value = _states.value.copy(bestSellersLoading = true)
                    }
                    is ApiResult.Success -> {
                        _states.value = _states.value.copy(
                            bestSellersList = result.data,
                            bestSellersLoading = false
                        )
                    }
                    is ApiResult.Failure -> {
                        _states.value = _states.value.copy(
                            errorMessage = result.error.message,
                            bestSellersLoading = false
                        )
                        _events.value = HomeContract.Events.ShowError(result.error.message ?: "An error occurred")
                    }
                }
            }
        }
    }

    private fun getLatestArrivals() {
        _states.value = _states.value.copy(latestArrivalsLoading = true)
        viewModelScope.launch {
            latestArrivalsUseCase(ProductSortKeys.CREATED_AT, true).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _states.value = _states.value.copy(latestArrivalsLoading = true)
                    }
                    is ApiResult.Success -> {
                        _states.value = _states.value.copy(
                            latestArrivals = result.data,
                            latestArrivalsLoading = false
                        )
                    }
                    is ApiResult.Failure -> {
                        _states.value = _states.value.copy(
                            errorMessage = result.error.message,
                            latestArrivalsLoading = false
                        )
                        _events.value = HomeContract.Events.ShowError(result.error.message ?: "An error occurred")
                    }
                }
            }
        }
    }

    fun resetEvent() {
        _events.value = HomeContract.Events.Idle
    }

}