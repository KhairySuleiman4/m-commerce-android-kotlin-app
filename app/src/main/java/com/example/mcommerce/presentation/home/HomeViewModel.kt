package com.example.mcommerce.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.DeleteFavoriteProductUseCase
import com.example.mcommerce.domain.usecases.GetBrandsUseCase
import com.example.mcommerce.domain.usecases.GetBestSellersUseCase
import com.example.mcommerce.domain.usecases.GetDiscountCodesUseCase
import com.example.mcommerce.domain.usecases.GetFavoriteProductsUseCase
import com.example.mcommerce.domain.usecases.GetLatestArrivalsUseCase
import com.example.mcommerce.domain.usecases.InsertProductToFavoritesUseCase
import com.example.mcommerce.domain.usecases.IsGuestModeUseCase
import com.example.mcommerce.presentation.products.ProductsContract
import com.example.mcommerce.presentation.utils.toSearchEntity
import com.example.mcommerce.type.ProductSortKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val brandsUseCase: GetBrandsUseCase,
    private val homeProductsUseCase: GetBestSellersUseCase,
    private val latestArrivalsUseCase: GetLatestArrivalsUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val insertProductToFavoritesUseCase: InsertProductToFavoritesUseCase,
    private val deleteFavoriteProductUseCase: DeleteFavoriteProductUseCase,
    private val getDiscountCodesUseCase: GetDiscountCodesUseCase
    private val isGuestModeUseCase: IsGuestModeUseCase
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
                if(isGuest()){
                    //_events.value = HomeContract.Events.ShowSnackbar("Login first so you can add to favorites")
                } else {
                    handleFavorite(action.product)
                }
            }
        }
    }

    private fun loadHomeData() {
        getCodes()
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

    private fun getCodes() {
        _states.value = _states.value.copy(codesLoading = true)
        viewModelScope.launch {
            getDiscountCodesUseCase().collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _states.value = _states.value.copy(codesLoading = true)
                    }
                    is ApiResult.Success -> {
                        _states.value = _states.value.copy(
                            codes = result.data?.map { it ?: "" } ?: listOf(),
                            codesLoading = false
                        )
                    }
                    is ApiResult.Failure -> {
                        _states.value = _states.value.copy(
                            errorMessage = result.error.message,
                            codesLoading = false
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
                        if(!isGuest()){
                            getFavorites()
                        }
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

    private fun getFavorites(){
        viewModelScope.launch(Dispatchers.IO) {
            getFavoriteProductsUseCase().collect{ result ->
                when(result){
                    is ApiResult.Failure -> {
                        _states.value = _states.value.copy(
                            errorMessage = result.error.message
                        )
                        _events.value = HomeContract.Events.ShowError(result.error.message ?: "An error occurred")
                    }
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        val newLatestArrivals = _states.value.latestArrivals.map { product ->
                            if(result.data.any { it.id == product.id }){
                                product.copy(isFavorite = true)
                            } else{
                                product
                            }
                        }
                        val newBestSellers = _states.value.bestSellersList.map { product ->
                            if(result.data.any { it.id == product.id }){
                                product.copy(isFavorite = true)
                            } else{
                                product
                            }
                        }
                        _states.value = _states.value.copy(
                            bestSellersList = newBestSellers,
                            latestArrivals = newLatestArrivals
                        )
                    }
                }
            }
        }
    }

    private fun handleFavorite(product: ProductsContract.ProductUIModel){
        viewModelScope.launch(Dispatchers.IO) {
            if(product.isFavorite){
                insertProductToFavoritesUseCase(product.toSearchEntity())
                updateFavoriteState(product)
            } else{
                deleteFavoriteProductUseCase(product.id)
                updateFavoriteState(product)
            }
        }
    }

    private fun updateFavoriteState(product: ProductsContract.ProductUIModel){
        val currentState = _states.value
        val updatedBestSellers = currentState.bestSellersList.map {
            if (it.id == product.id) {
                it.copy(isFavorite = !it.isFavorite)
            }
            else {
                it
            }
        }
        val updatedLatestArrivals = currentState.latestArrivals.map {
            if (it.id == product.id) {
                it.copy(isFavorite = !it.isFavorite)
            }
            else {
                it
            }
        }
        _states.value = _states.value.copy(
            bestSellersList = updatedBestSellers,
            latestArrivals = updatedLatestArrivals
        )
    }

    fun isGuest(): Boolean = isGuestModeUseCase()

    fun resetEvent() {
        _events.value = HomeContract.Events.Idle
    }

}