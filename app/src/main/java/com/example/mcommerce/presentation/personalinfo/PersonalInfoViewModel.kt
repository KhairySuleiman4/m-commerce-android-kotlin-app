package com.example.mcommerce.presentation.personalinfo

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetEmailUseCase
import com.example.mcommerce.domain.usecases.GetUserNameUseCase
import com.example.mcommerce.domain.usecases.UpdateUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getEmailUseCase: GetEmailUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase
) : ViewModel(), PersonalInfoContract.PersonalInfoViewModel {

    private val _events =
        mutableStateOf<PersonalInfoContract.Event>(PersonalInfoContract.Event.Idle)
    override val events: State<PersonalInfoContract.Event> get() = _events

    override fun invokeActions(action: PersonalInfoContract.Action) {
        when (action) {
            is PersonalInfoContract.Action.ClickOnSave -> {
                updateName(action.name)
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            val name = getUserNameUseCase()
            val email = getEmailUseCase()
            _events.value = PersonalInfoContract.Event.UpdateData(name, email)
        }
    }

    private fun updateName(name: String) {
        viewModelScope.launch {
            updateUserNameUseCase(name).collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _events.value = PersonalInfoContract.Event.ShowError(
                            it.error.message ?: "Couldn't update the name"
                        )
                    }

                    is ApiResult.Loading -> {
                        _events.value = PersonalInfoContract.Event.ShowError("Loading....")
                    }

                    is ApiResult.Success -> {
                        _events.value = PersonalInfoContract.Event.SaveDone(it.data)
                    }
                }
            }
        }
    }
}