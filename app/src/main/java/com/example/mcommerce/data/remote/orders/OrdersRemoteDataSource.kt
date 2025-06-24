package com.example.mcommerce.data.remote.orders

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

interface OrdersRemoteDataSource {
    fun getOrders(accessToken: String): Flow<ApiResult<List<OrderEntity>>>
}