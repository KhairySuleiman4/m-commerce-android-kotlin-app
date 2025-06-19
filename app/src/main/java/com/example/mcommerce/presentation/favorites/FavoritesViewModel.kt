package com.example.mcommerce.presentation.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.DeleteFavoriteProductUseCase
import com.example.mcommerce.domain.usecases.GetFavoriteProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val deleteFavoriteProductUseCase: DeleteFavoriteProductUseCase
): ViewModel() {
    init {
        getFavoriteProducts()
    }

    fun getFavoriteProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            getFavoriteProductsUseCase().collect{
                Log.i("TAG", "getFavoriteProducts: $it")
                delay(3000)
                when(it){
                    is ApiResult.Failure -> {}
                    is ApiResult.Loading -> {}
                    is ApiResult.Success -> {
                        deleteFavoriteProductUseCase(it.data[0].id)
                    }
                }
            }
        }
    }
}