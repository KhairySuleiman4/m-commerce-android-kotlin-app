package com.example.mcommerce.presentation.product_info

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetProductByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductInfoViewModel @Inject constructor(
    private val useCase: GetProductByIdUseCase
) : ViewModel(), ProductInfoContract.ProductInfoViewModel {

    private val _states = mutableStateOf<ProductInfoContract.States>(ProductInfoContract.States.Loading)
    private val _events = mutableStateOf<ProductInfoContract.Events>(ProductInfoContract.Events.Idle)

    override val states: State<ProductInfoContract.States>
        get() = _states
    override val events: State<ProductInfoContract.Events>
        get() = _events

    fun getProductById(id: String){
        viewModelScope.launch {
            useCase(id).collect{ result ->
                when(result){
                    is ApiResult.Failure -> {
                        _states.value = ProductInfoContract.States.Failure(result.error.message.toString())
                    }
                    is ApiResult.Loading -> {
                        _states.value = ProductInfoContract.States.Loading
                    }
                    is ApiResult.Success -> {
                        if(result.data == null){
                            _states.value = ProductInfoContract.States.Failure("Product Not Found")
                        } else{
                            _states.value = ProductInfoContract.States.Success(result.data)
                        }
                    }
                }
            }
        }
    }

    override fun invokeActions(action: ProductInfoContract.Action) {
        when(action){
            is ProductInfoContract.Action.ClickOnAddToCart -> {
                _events.value = ProductInfoContract.Events.ShowSnackbar("Success")
            }
            is ProductInfoContract.Action.ClickOnAddToWishList -> {
                _events.value = ProductInfoContract.Events.ShowSnackbar("Success")
            }
        }
    }

    fun resetEvent(){
        _events.value = ProductInfoContract.Events.Idle
    }
}