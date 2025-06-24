package com.example.mcommerce.presentation.settings

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.ExchangeRateEntity

interface SettingsContract {
    interface SettingsViewModel {
        fun invokeActions(action: Action)
        val states: State<States>
        val events: State<Events>
    }

    sealed interface Action {
        data object ClickOnCurrency : Action
        data object ClickOnHide : Action
        data class ClickOnSelectedCurrency(val currency: String) : Action
        data class ClickOnCheckButton(val isChecked: Boolean) : Action
    }

    sealed interface States {
        data object Loading : States
        data class Success(val rates: ExchangeRateEntity) : States
        data class Failure(val errorMessage: String) : States
        data object Idle : States
    }

    sealed interface Events {
        data object CheckDarkMode : Events
        data object UnCheckDarkMode : Events
        data object ShowCurrencyCatalog : Events
        data object HideCurrencyCatalog : Events
        data class SelectCurrency(val currency: String) : Events
        data class SaveCurrency(val currency: String) : Events
        data class ShowError(val msg: String) : Events
        data object Idle : Events
    }
}