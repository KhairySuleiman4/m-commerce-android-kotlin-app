package com.example.mcommerce.presentation.addresses

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.ChangeDefaultAddressUseCase
import com.example.mcommerce.domain.usecases.GetCustomerAddressesUseCase
import com.example.mcommerce.domain.usecases.GetUserAccessTokenUseCase
import com.example.mcommerce.domain.usecases.RemoveAddressUseCase
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressesViewModel @Inject constructor(
    private val changeDefaultAddressUseCase: ChangeDefaultAddressUseCase,
    private val getCustomerAddressesUseCase: GetCustomerAddressesUseCase,
    private val removeAddressUseCase: RemoveAddressUseCase,
    private val getUserAccessTokenUseCase: GetUserAccessTokenUseCase
) : ViewModel(), AddressesContract.AddressesViewModel {

    private val _states = mutableStateOf<AddressesContract.States>(AddressesContract.States.Idle)
    private val _events = mutableStateOf<AddressesContract.Events>(AddressesContract.Events.Idle)

    override val states: State<AddressesContract.States> get() = _states
    override val events: State<AddressesContract.Events> get() = _events

    private var token = ""
    override fun invokeActions(action: AddressesContract.Action) {
        when (action) {
            is AddressesContract.Action.ClickOnDelete -> {
                deleteAddress(action.addressId)
            }

            is AddressesContract.Action.ClickOnSetDefault -> {
                setDefault(action.addressId)
            }
        }
    }

    fun getAddresses() {
        viewModelScope.launch {
            if (token.isBlank()) {
                val accessToken = getUserAccessTokenUseCase()
                val json = Gson().fromJson(accessToken, JsonObject::class.java)
                token = json.get("token")?.asString ?: ""
            }
            getCustomerAddressesUseCase(token).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _states.value = AddressesContract.States.Failure(
                            it.error.message ?: "there is something wrong"
                        )
                    }

                    is ApiResult.Loading -> {
                        _states.value = AddressesContract.States.Loading
                    }

                    is ApiResult.Success -> {
                        _states.value = AddressesContract.States.Success(it.data ?: listOf())
                    }
                }
            }
        }
    }

    private fun setDefault(addressId: String) {
        viewModelScope.launch {
            if (token.isBlank()) {
                val accessToken = getUserAccessTokenUseCase()
                val json = Gson().fromJson(accessToken, JsonObject::class.java)
                token = json.get("token")?.asString ?: ""
            }
            changeDefaultAddressUseCase(token, addressId).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _events.value = AddressesContract.Events.ShowError(
                            it.error.message ?: "Couldn't change The default address"
                        )
                    }

                    is ApiResult.Loading -> {
                        _events.value = AddressesContract.Events.ShowError("Saving...")
                    }

                    is ApiResult.Success -> {
                        _events.value = AddressesContract.Events.ShowError("Saved Successfully!")
                        delay(1000)
                        getAddresses()
                    }
                }
            }
        }
    }

    private fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            if (token.isBlank()) {
                val accessToken = getUserAccessTokenUseCase()
                val json = Gson().fromJson(accessToken, JsonObject::class.java)
                token = json.get("token")?.asString ?: ""
            }
            removeAddressUseCase(token, addressId).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _events.value = AddressesContract.Events.ShowError(
                            it.error.message ?: "Couldn't delete the address"
                        )
                    }

                    is ApiResult.Loading -> {
                        _events.value = AddressesContract.Events.ShowError("Saving Changes...")
                    }

                    is ApiResult.Success -> {
                        _events.value = AddressesContract.Events.ShowError("Deleted Successfully!")
                        delay(1000)
                        getAddresses()
                    }
                }
            }
        }
    }

}