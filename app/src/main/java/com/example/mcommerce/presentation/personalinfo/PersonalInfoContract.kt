package com.example.mcommerce.presentation.personalinfo

import androidx.compose.runtime.State


sealed interface PersonalInfoContract {
    interface PersonalInfoViewModel {
        fun invokeActions(action: Action)
        val events: State<Event>
    }

    sealed interface Action {
        data class ClickOnSave(val name: String) : Action
    }

    sealed interface Event {
        data object Idle : Event
        data class SaveDone(val name: String) : Event
        data class ShowError(val msg: String) : Event
        data class UpdateData(val name: String, val email: String) : Event
    }
}