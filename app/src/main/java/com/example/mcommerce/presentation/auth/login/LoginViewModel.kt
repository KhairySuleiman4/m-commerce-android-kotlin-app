package com.example.mcommerce.presentation.auth.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.LoginUseCase
import com.example.mcommerce.presentation.auth.AuthContract
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val useCase: LoginUseCase
): ViewModel(), AuthContract.LoginViewModel {
    private val _events = mutableStateOf<AuthContract.Events>(AuthContract.Events.Idle)
    override val events: State<AuthContract.Events> get() = _events

    init {
        Log.i("TAG", "LoginViewModel: ${FirebaseAuth.getInstance().currentUser}")
    }

    override fun invokeActions(action: AuthContract.LoginAction) {
        when(action){
            is AuthContract.LoginAction.ClickOnContinueAsGuest -> {
                _events.value = AuthContract.Events.NavigateToHome
            }
            is AuthContract.LoginAction.ClickOnNavigateToSignup -> {
                _events.value = AuthContract.Events.NavigateToSignup
            }
            is AuthContract.LoginAction.ClickOnLoginButton -> {
                changeEventBasedOnCredentials(action.email, action.password)
            }
        }
    }

    private fun changeEventBasedOnCredentials(email: String, password: String){
        val msg = checkCredentials(email, password)
        when(msg){
            "Success" -> {
                tryToLogin(email, password)
            }
            "Complete" -> {
                _events.value = AuthContract.Events.ShowSnackbar("Please, fill both fields")
            }
            "Email" -> {
                _events.value = AuthContract.Events.ShowSnackbar("Invalid email")
            }
            "Password" -> {
                _events.value = AuthContract.Events.ShowSnackbar("Invalid password")
            }
        }
    }

    private fun tryToLogin(email: String, password: String){
        viewModelScope.launch{
            _events.value = AuthContract.Events.ShowLoading

            try {
                val loginResult = useCase(email, password).first { it !is ApiResult.Loading }

                when (loginResult) {
                    is ApiResult.Loading -> {
                        return@launch
                    }
                    is ApiResult.Failure -> {
                        _events.value = AuthContract.Events.ShowSnackbar(
                            "Logging in failed: ${loginResult.error.message}"
                        )
                    }
                    is ApiResult.Success -> {
                        _events.value = AuthContract.Events.NavigateToHome
                    }
                }

            } catch (e: Exception){
                _events.value = AuthContract.Events.ShowSnackbar(
                    "Logging in failed: ${e.message}"
                )
            }
        }
    }

    private fun checkCredentials(email: String, password: String): String{
        return when {
            email.isBlank() || password.isBlank() ->
                "Complete"

            isNotValidEmail(email) -> "Email"
            isNotValidPassword(password) -> "Password"

            else -> "Success"
        }
    }

    private fun isNotValidEmail(mail: String): Boolean = !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()

    private fun isNotValidPassword(password: String): Boolean = password.length < 6

    fun resetEvent(){
        _events.value = AuthContract.Events.Idle
    }
}