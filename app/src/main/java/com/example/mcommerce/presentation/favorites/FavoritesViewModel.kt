package com.example.mcommerce.presentation.favorites

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.DeleteFavoriteProductUseCase
import com.example.mcommerce.domain.usecases.GetFavoriteProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val deleteFavoriteProductUseCase: DeleteFavoriteProductUseCase
): ViewModel(), FavoritesContract.FavoritesViewModel {

    private val _states = mutableStateOf<FavoritesContract.States>(FavoritesContract.States.Idle)
    private val _events = mutableStateOf<FavoritesContract.Events>(FavoritesContract.Events.Idle)

    override val states: State<FavoritesContract.States> get() = _states
    override val events: State<FavoritesContract.Events> get() = _events

    override fun invokeActions(action: FavoritesContract.Action) {
        when(action){
            is FavoritesContract.Action.ClickOnAddToCart -> {

            }
            is FavoritesContract.Action.ClickOnDeleteFromFavorite -> {
                deleteProduct(action.productId)
            }
            is FavoritesContract.Action.ClickOnProduct -> {
                _events.value = FavoritesContract.Events.NavigateToProductInfo(action.productId)
            }
        }
    }

    fun getFavoriteProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            getFavoriteProductsUseCase().collect{ result ->
                when(result){
                    is ApiResult.Failure -> {

                    }
                    is ApiResult.Loading -> {
                        _states.value = FavoritesContract.States.Loading
                    }
                    is ApiResult.Success -> {
                        _states.value = FavoritesContract.States.Success(result.data)
                    }
                }
            }
        }
    }

    private fun deleteProduct(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            deleteFavoriteProductUseCase(id)
            getFavoriteProducts()
        }
    }

    fun resetEvent() {
        _events.value = FavoritesContract.Events.Idle
    }
}