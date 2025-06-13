package com.example.mcommerce.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetBrandsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val brandsUseCase: GetBrandsUseCase
): ViewModel(), HomeContract.HomeViewModel {

    private val _states = mutableStateOf<HomeContract.States>(HomeContract.States.Idle)
    private val _events = mutableStateOf<HomeContract.Events>(HomeContract.Events.Idle)

    override val states: State<HomeContract.States> get() = _states
    override val events: State<HomeContract.Events> get() = _events

    fun getBrands(){
        viewModelScope.launch {
            brandsUseCase().collect{
                when(it){
                    is ApiResult.Failure -> {
                        _states.value = HomeContract.States.Failure(it.error.message.toString())
                    }
                    is ApiResult.Loading -> {
                        _states.value = HomeContract.States.Loading
                    }
                    is ApiResult.Success -> {
                        _states.value = HomeContract.States.Success(it.data)
                    }
                }
            }
        }
    }

    override fun invokeActions(action: HomeContract.Action) {
        when(action){
            is HomeContract.Action.ClickOnBrand -> {
                _events.value = HomeContract.Events.NavigateToBrandProducts(action.brandId, action.brandName)
            }
            HomeContract.Action.ClickOnCart -> {
                //navigate to cart and don't forget to switch branches
            }
        }
    }

    fun resetEvent() {
        _events.value = HomeContract.Events.Idle
    }

}