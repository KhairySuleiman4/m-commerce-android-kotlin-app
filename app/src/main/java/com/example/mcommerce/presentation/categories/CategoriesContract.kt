package com.example.mcommerce.presentation.categories

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.CategoriesEntity


interface CategoriesContract {
    interface CategoriesViewModel{
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action{
        data object ClickOnCart: Action
        data class ClickOnCategory(val categoryId: String): Action
    }

    sealed interface States{
        data object Loading: States
        data class Success(val categoriesList: List<CategoriesEntity>): States
        data class Failure(val errorMessage: String): States
        data object Idle: States
    }

    sealed interface Events{
        data class NavigateToCategoryProducts(val categoryId: String): Events
        data object Idle: Events
    }
}