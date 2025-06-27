package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.entities.LineEntity
import com.example.mcommerce.domain.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

interface OrdersRepo {
    fun createOrder(
        email: String,
        items: List<LineEntity>,
        address: AddressEntity,
        code: String
    ): Flow<ApiResult<Boolean>>
    suspend fun getOrders(accessToken: String): Flow<ApiResult<List<OrderEntity>>>
}