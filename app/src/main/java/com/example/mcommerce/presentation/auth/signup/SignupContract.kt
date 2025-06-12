package com.example.mcommerce.presentation.auth.signup

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.UserCredentialsEntity

interface SignupContract {
    interface SignupViewModel{
        fun invokeActions(action: Action.ClickOnSignup)
        val events: State<Events>
    }

    sealed interface Action{
        data class ClickOnSignup(val credentials: UserCredentialsEntity, val confirmPassword: String)
    }

    sealed interface Events{
        data object Idle: Events
        data object NavigateToHome: Events
        data object ShowLoading: Events
        data class ShowSnackbar(val message: String): Events
    }
}