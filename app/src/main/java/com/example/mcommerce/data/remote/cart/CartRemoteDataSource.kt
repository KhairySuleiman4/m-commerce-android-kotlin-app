package com.example.mcommerce.data.remote.cart

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CartEntity
import kotlinx.coroutines.flow.Flow

interface CartRemoteDataSource {
    fun getCartById(cartId: String): Flow<ApiResult<CartEntity?>>
    fun createCart(accessToken: String, email: String): Flow<ApiResult<CartEntity?>>
    fun addItemToCart(cartId: String, quantity: Int, itemId: String): Flow<ApiResult<CartEntity?>>
    fun removeItemFromCart(cartId: String, itemId: String): Flow<ApiResult<CartEntity?>>
    fun changeQuantityOfItemInCart(
        cartId: String,
        quantity: Int,
        itemId: String
    ): Flow<ApiResult<CartEntity?>>

    fun addDiscountCodeToCart(cartId: String, code: String): Flow<ApiResult<CartEntity?>>
}