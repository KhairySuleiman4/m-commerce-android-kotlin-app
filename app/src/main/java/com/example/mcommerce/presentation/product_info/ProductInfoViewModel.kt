package com.example.mcommerce.presentation.product_info

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mcommerce.data.mappers.toSearchEntity
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.usecases.AddItemToCartUseCase
import com.example.mcommerce.domain.usecases.DeleteFavoriteProductUseCase
import com.example.mcommerce.domain.usecases.GetCartUseCase
import com.example.mcommerce.domain.usecases.GetFavoriteProductsUseCase
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.domain.usecases.GetProductByIdUseCase
import com.example.mcommerce.domain.usecases.InsertProductToFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductInfoViewModel @Inject constructor(
    private val addItemToCartUseCase: AddItemToCartUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val getProductUseCase: GetProductByIdUseCase,
    private val insertToFavoritesUseCase: InsertProductToFavoritesUseCase,
    private val getFavoritesUseCase: GetFavoriteProductsUseCase,
    private val deleteFavoriteProductUseCase: DeleteFavoriteProductUseCase,
    private val getCurrencyUseCase: GetCurrentCurrencyUseCase,
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase
) : ViewModel(), ProductInfoContract.ProductInfoViewModel {

    private val _states = mutableStateOf<ProductInfoContract.States>(ProductInfoContract.States.Loading)
    private val _events = mutableStateOf<ProductInfoContract.Events>(ProductInfoContract.Events.Idle)

    override val states: State<ProductInfoContract.States>
        get() = _states
    override val events: State<ProductInfoContract.Events>
        get() = _events

    fun getProductById(id: String){
        viewModelScope.launch {
            getProductUseCase(id).collect{ result ->
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
                            getCart()
                            getFavorites()
                        }
                    }
                }
            }
        }
    }

    private fun getCart() {
        viewModelScope.launch {
            getCartUseCase().collect { result ->
                when (result) {
                    is ApiResult.Failure -> {

                    }

                    is ApiResult.Loading -> {

                    }

                    is ApiResult.Success -> {
                        if (result.data != null) {
                            if (_states.value is ProductInfoContract.States.Success) {
                                val product =
                                    (_states.value as ProductInfoContract.States.Success).product
                                val newProduct= product.copy(variants = product.variants.map { item ->
                                    if (result.data.items.any { it.id == item.id })
                                        item.isSelected = true
                                    item
                                })
                                _states.value = ProductInfoContract.States.Success(newProduct)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getFavorites(){
        viewModelScope.launch(Dispatchers.IO){
            getFavoritesUseCase().collect{ result ->
                when(result){
                    is ApiResult.Failure -> {

                    }
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        val favoriteIds = result.data.map { it.id }.toSet()
                        if (_states.value is ProductInfoContract.States.Success) {
                            val product = (_states.value as ProductInfoContract.States.Success).product
                            val isFavorite = favoriteIds.contains(product.id)
                            val newProduct = product.copy(isFavorite = isFavorite)
                            _states.value = ProductInfoContract.States.Success(newProduct)
                        }
                    }
                }
            }
        }
    }

    fun getCurrency(){
        viewModelScope.launch {
            val currency = getCurrencyUseCase()
            val rate = getCurrentExchangeRateUseCase()
            _events.value = ProductInfoContract.Events.ShowCurrency(currency, rate)
        }
    }

    private fun addItemToCart(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            addItemToCartUseCase(id,1).collect{
                when(it){
                    is ApiResult.Failure -> {
                        _events.value = ProductInfoContract.Events.ShowSnackbar(it.error.message.toString())
                    }
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        if (_states.value is ProductInfoContract.States.Success){
                            val product =
                                (_states.value as ProductInfoContract.States.Success).product
                            val newProduct = product.copy(variants = product.variants.map {
                              if (it.id==id)
                                  it.isSelected = true
                              it
                          })
                            _states.value = ProductInfoContract.States.Success(newProduct)
                            _events.value = ProductInfoContract.Events.ShowSnackbar("Success")
                        }
                    }
                }
            }
        }
    }

    override fun invokeActions(action: ProductInfoContract.Action) {
        when(action){
            is ProductInfoContract.Action.ClickOnAddToCart -> {
               addItemToCart(action.variant.id)
                _events.value = ProductInfoContract.Events.ShowSnackbar("Added to cart")
            }
            is ProductInfoContract.Action.ClickOnAddToWishList -> {
                _events.value = ProductInfoContract.Events.ShowSnackbar("Added to favorites")
                viewModelScope.launch(Dispatchers.IO) {
                    if(action.product.isFavorite){
                        insertToFavoritesUseCase(action.product.toSearchEntity())
                        _states.value = ProductInfoContract.States.Success(action.product)
                    } else {
                        deleteFavoriteProductUseCase(action.product.id)
                        _states.value = ProductInfoContract.States.Success(action.product)
                    }
                }
            }
        }
    }

    fun resetEvent(){
        _events.value = ProductInfoContract.Events.Idle
    }
}