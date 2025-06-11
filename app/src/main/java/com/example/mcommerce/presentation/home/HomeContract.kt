package com.example.mcommerce.presentation.home

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.CollectionsEntity

interface HomeContract {
    interface HomeViewModel{
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action{
        data object ClickOnCart: Action
        data class ClickOnBrand(val brandId: String, val brandName: String): Action
    }

    sealed interface States{
        data object Loading: States
        data class Success(val brandsList: List<CollectionsEntity>): States
        data class Failure(val errorMessage: String): States
        data object Idle: States
    }

    sealed interface Events{
        data class NavigateToBrandDetails(val brandId: String, val brandName: String): Events
        data object Idle: Events
    }
}