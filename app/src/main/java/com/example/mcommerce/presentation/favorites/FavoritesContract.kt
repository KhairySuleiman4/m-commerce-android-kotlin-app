package com.example.mcommerce.presentation.favorites

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.ProductSearchEntity

interface FavoritesContract {
    interface FavoritesViewModel {
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action {
        data class ClickOnProduct(val productId: String) : Action
        data class ClickOnDeleteFromFavorite(val productId: String) : Action
    }

    sealed interface States {
        data object Loading : States
        data object Idle : States
        data class Success(val products: List<ProductSearchEntity>) : States
        data class Failure(val errorMsg: String) : States
    }

    sealed interface Events {
        data object Idle : Events
        data class NavigateToProductInfo(val productId: String) : Events
        data class ShowCurrency(val currency: String, val rate: Double) : Events
    }
}