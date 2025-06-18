package com.example.mcommerce.presentation.products

import androidx.compose.runtime.State

interface ProductsContract {

    interface ProductsViewModel{
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action{
        data class ClickOnProduct(val productId: String): Action
        data class ClickOnFavorite(val productId: String): Action
        data class ClickOnAddToCart(val variantId: String): Action
    }

    sealed interface States{
        data object Loading: States
        data class Success(val productsList: List<ProductUIModel>): States
        data class Failure(val errorMessage: String): States
        data object Idle: States
    }

    sealed interface Events{
        data object Idle: Events
        data class NavigateToProductDetails(val productId: String): Events
        data class ShowSnackbar(val message: String) : Events
    }

    data class ProductUIModel(
        val id: String,
        val title: String,
        val imageUrl: String,
        val price: String,
        val variantId: String,
        val isFavorite: Boolean = false,
        val isInCart: Boolean = false
    )
}