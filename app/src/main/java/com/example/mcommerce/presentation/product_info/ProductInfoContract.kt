package com.example.mcommerce.presentation.product_info

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductVariantEntity

interface ProductInfoContract {
    interface ProductInfoViewModel{
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action{
        data class ClickOnAddToCart(val variant: ProductVariantEntity): Action
        data class ClickOnAddToWishList(val product: ProductInfoEntity): Action
    }

    sealed interface States{
        data object Loading: States
        data class Success(val product: ProductInfoEntity): States
        data class Failure(val errorMessage: String): States
        data object Idle: States
    }

    sealed interface Events{
        data object Idle: Events
        data class ShowSnackbar(val message: String): Events
    }
}