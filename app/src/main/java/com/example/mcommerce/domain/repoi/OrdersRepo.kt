package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

interface OrdersRepo {
    suspend fun getOrders(accessToken: String): Flow<ApiResult<List<OrderEntity>>>
}