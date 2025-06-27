package com.example.mcommerce.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.usecases.ClearLocalCartUseCase
import com.example.mcommerce.domain.usecases.GetEmailUseCase
import com.example.mcommerce.domain.usecases.GetUserNameUseCase
import com.example.mcommerce.domain.usecases.IsGuestModeUseCase
import com.example.mcommerce.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val clearLocalCartUseCase: ClearLocalCartUseCase,
    private val isGuestModeUseCase: IsGuestModeUseCase,
    private val getEmailUseCase: GetEmailUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel(), ProfileContract.ProfileViewModel {

    private val _events = mutableStateOf<ProfileContract.Event>(ProfileContract.Event.Idle)

    override val events: State<ProfileContract.Event> get() = _events

    override fun invokeActions(action: ProfileContract.Action) {
        when (action) {
            ProfileContract.Action.ClickOnLogout -> {
                logoutUseCase()
                clearLocalCartUseCase()
                _events.value = ProfileContract.Event.Logout
            }
        }
    }

    fun setup() {
        viewModelScope.launch {
            val isGuest = isGuestModeUseCase()
            if (!isGuest) {
                val email = getEmailUseCase()
                val name = getUserNameUseCase()
                _events.value = ProfileContract.Event.UpdateData(isGuest, email, name)
            } else
                _events.value = ProfileContract.Event.UpdateData(isGuest, "", "Guest")
        }
    }

    fun resetEvent() {
        _events.value = ProfileContract.Event.Idle
    }
}