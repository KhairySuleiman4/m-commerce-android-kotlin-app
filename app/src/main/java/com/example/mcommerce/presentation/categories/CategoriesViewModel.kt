package com.example.mcommerce.presentation.categories

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesUseCase: GetCategoriesUseCase
) : ViewModel(), CategoriesContract.CategoriesViewModel {

    private val _states = mutableStateOf<CategoriesContract.States>(CategoriesContract.States.Idle)
    private val _events = mutableStateOf<CategoriesContract.Events>(CategoriesContract.Events.Idle)

    override val states: State<CategoriesContract.States> get() = _states
    override val events: State<CategoriesContract.Events> get() = _events

    fun getCategories() {
        viewModelScope.launch {
            categoriesUseCase().collect {
                when (it) {
                    is ApiResult.Failure -> {
                        _states.value =
                            CategoriesContract.States.Failure(it.error.message.toString())
                    }

                    is ApiResult.Loading -> {
                        _states.value = CategoriesContract.States.Loading
                    }

                    is ApiResult.Success -> {
                        _states.value = CategoriesContract.States.Success(it.data)
                    }
                }
            }
        }
    }

    override fun invokeActions(action: CategoriesContract.Action) {
        when (action) {
            is CategoriesContract.Action.ClickOnCategory -> {
                _events.value = CategoriesContract.Events.NavigateToCategoryProducts(
                    action.collectionId,
                    action.collectionName
                )
            }
        }
    }

    fun resetEvent() {
        _events.value = CategoriesContract.Events.Idle
    }

}