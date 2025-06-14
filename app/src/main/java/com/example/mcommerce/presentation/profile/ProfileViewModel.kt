package com.example.mcommerce.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mcommerce.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
): ViewModel(), ProfileContract.ProfileViewModel {

    private val _events = mutableStateOf<ProfileContract.Event>(ProfileContract.Event.Idle)

    override val events: State<ProfileContract.Event> get() = _events

    override fun invokeActions(action: ProfileContract.Action) {
        when(action){
            ProfileContract.Action.ClickOnLogout -> {
                logoutUseCase()
                _events.value = ProfileContract.Event.Logout
            }
        }
    }

    fun resetEvent(){
        _events.value = ProfileContract.Event.Idle
    }
}