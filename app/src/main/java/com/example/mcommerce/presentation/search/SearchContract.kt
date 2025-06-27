package com.example.mcommerce.presentation.search

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.ProductSearchEntity

interface SearchContract {
    interface SearchViewModel {
        fun invokeActions(action: Action)
        val events: State<Events>
    }

    sealed interface Action {
        data class OnSearchQueryChanged(val query: String) : Action
        data class OnTypeSelected(val type: String?) : Action
        data class OnBrandSelected(val brand: String?) : Action
        data class OnPriceRangeChanged(val min: Double?, val max: Double?) : Action
        data class ClickOnFavoriteIcon(val product: ProductSearchEntity) : Action
    }

    sealed interface States {

    }

    sealed interface Events {
        data object Idle : Events
        data class ShowCurrency(val currency: String, val rate: Double) : Events
        data class ShowSnackbar(val msg: String) : Events
    }

    data class ProductState(
        val allProducts: List<ProductSearchEntity> = emptyList(),
        val filteredProducts: List<ProductSearchEntity> = emptyList(),
        val filter: ProductFilter = ProductFilter(),
        val brands: List<String> = emptyList(),
        val isLoading: Boolean = false,
        val errorMsg: String? = null
    )

    data class ProductFilter(
        val type: String? = null,
        val brand: String? = null,
        val minPrice: Double? = null,
        val maxPrice: Double? = null,
        val searchQuery: String = ""
    )
}