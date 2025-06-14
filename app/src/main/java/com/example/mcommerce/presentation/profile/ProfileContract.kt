package com.example.mcommerce.presentation.profile

import androidx.compose.runtime.State

interface ProfileContract {

    interface ProfileViewModel{
        fun invokeActions(action: Action)
        val events: State<Event>
    }

    sealed interface Action{
        data object ClickOnLogout: Action
    }

    sealed interface Event{
        data object Logout: Event
        data object Idle: Event
    }
}