package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CartEntity
import kotlinx.coroutines.flow.Flow

interface CartRepo {
    fun getCart(): Flow<ApiResult<CartEntity?>>
    fun addItemToCart(itemId: String, quantity: Int): Flow<ApiResult<CartEntity?>>
    fun removeItemFromCart(itemId: String): Flow<ApiResult<CartEntity?>>
    suspend fun changeItem(itemId: String, quantity: Int): Flow<ApiResult<Boolean>>
    fun addDiscountCode(code: String,): Flow<ApiResult<CartEntity?>>
    fun clearLocalCart(): Boolean
    suspend fun removeCart(): Flow<ApiResult<Boolean>>
}