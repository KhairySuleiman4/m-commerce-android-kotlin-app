package com.example.mcommerce.presentation.auth

import androidx.compose.runtime.State
import com.example.mcommerce.domain.entities.UserCredentialsEntity

interface AuthContract {
    interface SignupViewModel {
        fun invokeActions(action: SignupAction)
        val events: State<Events>
    }

    interface LoginViewModel {
        fun invokeActions(action: LoginAction)
        val events: State<Events>
    }

    sealed interface SignupAction {
        data object ClickOnNavigateToLogin : SignupAction
        data object ClickOnContinueAsGuest : SignupAction
        data class ClickOnSignupButton(
            val credentials: UserCredentialsEntity,
            val confirmPassword: String
        ) : SignupAction
    }

    sealed interface LoginAction {
        data object ClickOnNavigateToSignup : LoginAction
        data object ClickOnContinueAsGuest : LoginAction
        data class ClickOnLoginButton(val email: String, val password: String) : LoginAction
    }

    sealed interface Events {
        data object Idle : Events
        data object NavigateToHomeGuest : Events
        data object ShowLoading : Events
        data class ShowSnackbar(val message: String) : Events
        data object NavigateToLogin : Events
        data object NavigateToSignup : Events
        data object NavigateToHomeUser : Events
    }
}