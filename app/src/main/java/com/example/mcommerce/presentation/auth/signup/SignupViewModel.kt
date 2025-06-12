package com.example.mcommerce.presentation.auth.signup

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import com.example.mcommerce.domain.usecases.CreateNewAccountOnFirebaseUseCase
import com.example.mcommerce.domain.usecases.CreateNewCustomerOnShopifyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor (
    private val shopifyUseCase: CreateNewCustomerOnShopifyUseCase,
    private val firebaseUseCase: CreateNewAccountOnFirebaseUseCase
): ViewModel(), SignupContract.SignupViewModel {

    private val _events = mutableStateOf<SignupContract.Events>(SignupContract.Events.Idle)

    override val events: State<SignupContract.Events> get() = _events

    override fun invokeActions(action: SignupContract.Action.ClickOnSignup) {

        val msg = checkCredentials(action.credentials, action.confirmPassword)
        when(msg){
            "Success" -> {
                createNewAccount(action.credentials)
            }
            "Complete" -> {
                _events.value = SignupContract.Events.ShowSnackbar("Please, fill all fields")
            }
            "Email" -> {
                _events.value = SignupContract.Events.ShowSnackbar("Please, enter a valid email")
            }
            "Phone" -> {
                _events.value = SignupContract.Events.ShowSnackbar("Please, enter a valid phone number")
            }
            "Password" -> {
                _events.value = SignupContract.Events.ShowSnackbar("Password should be 6 characters at least")
            }
            "Confirm" -> {
                _events.value = SignupContract.Events.ShowSnackbar("The two passwords should be exactly the same")
            }
        }
    }

    private fun checkCredentials(credentials: UserCredentialsEntity, confirmPassword: String): String {
        return when {
            credentials.name.isBlank() || credentials.mail.isBlank() || credentials.phoneNumber.isBlank() || credentials.password.isBlank() || confirmPassword.isBlank() ->
                "Complete"

            isNotValidEmail(credentials.mail) -> "Email"
            isNotValidPhoneNumber(credentials.phoneNumber) -> "Phone"
            isNotValidPassword(credentials.password) -> "Password"
            credentials.password != confirmPassword -> "Confirm"

            else -> "Success"
        }
    }


    private fun createNewAccount(credentials: UserCredentialsEntity) {
        viewModelScope.launch {
            _events.value = SignupContract.Events.ShowLoading

            try {
                val shopifyResult = shopifyUseCase(credentials)
                    .first { it !is ApiResult.Loading }

                checkShopifyResult(shopifyResult, credentials)
            } catch (e: Exception) {
                _events.value = SignupContract.Events.ShowSnackbar(
                    "Account creation failed: ${e.message}"
                )
            }
        }
    }

    private suspend fun checkShopifyResult(shopifyResult:  ApiResult<String>, credentials: UserCredentialsEntity){
        when (shopifyResult) {
            is ApiResult.Loading -> {
                return
            }
            is ApiResult.Failure -> {
                _events.value = SignupContract.Events.ShowSnackbar(
                    "Shopify account creation failed: ${shopifyResult.error.message}"
                )
                return
            }
            is ApiResult.Success -> {
                val firebaseResult = firebaseUseCase(
                    UserCredentialsEntity(
                        credentials.name,
                        credentials.mail,
                        credentials.phoneNumber,
                        credentials.password,
                        credentials.isVerified,
                        shopifyResult.data
                    )
                ).first { it !is ApiResult.Loading }

                checkFirebaseResult(firebaseResult)
            }
        }
    }

    private fun checkFirebaseResult(firebaseResult: ApiResult<Boolean>, ){
        when (firebaseResult) {
            is ApiResult.Loading -> {
                return
            }
            is ApiResult.Failure -> {
                _events.value = SignupContract.Events.ShowSnackbar(
                    "Firebase account creation failed: ${firebaseResult.error.message}"
                )
            }
            is ApiResult.Success -> {
                _events.value = SignupContract.Events.NavigateToHome
            }
        }
    }

    private fun isNotValidEmail(mail: String): Boolean = !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()

    private fun isNotValidPhoneNumber(phone: String): Boolean = !Regex("^(010|011|012|015)\\d{8}$").matches(phone)

    private fun isNotValidPassword(password: String): Boolean = password.length < 6

    fun resetEvent(){
        _events.value = SignupContract.Events.Idle
    }
}