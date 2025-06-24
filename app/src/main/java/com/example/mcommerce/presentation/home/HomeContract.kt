package com.example.mcommerce.presentation.home

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.presentation.products.ProductsContract

interface HomeContract {
    interface HomeViewModel{
        fun invokeActions(action: Action)
        val states: State<HomeState>
        val events: State<Events>
    }

    sealed interface Action{
        data object LoadHomeData : Action
        data class ClickOnBrand(val brandId: String, val brandName: String): Action
        data class ClickOnProduct(val productId: String): Action
        data class ClickOnFavorite(val product: ProductsContract.ProductUIModel): Action
    }

    data class HomeState(
        val isLoading: Boolean = false,
        val brandsList: List<CollectionsEntity> = emptyList(),
        val bestSellersList: List<ProductsEntity> = emptyList(),
        val latestArrivals: List<ProductsEntity> = listOf(),
        val codes: List<String> = listOf(),
        val brandsLoading: Boolean = false,
        val bestSellersLoading: Boolean = false,
        val latestArrivalsLoading: Boolean = false,
        val codesLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Events{
        data class NavigateToBrandProducts(val brandId: String, val brandName: String): Events
        data class NavigateToProductDetails(val productId: String): Events
        data class ShowError(val message: String) : Events
        data object Idle: Events
    }
}