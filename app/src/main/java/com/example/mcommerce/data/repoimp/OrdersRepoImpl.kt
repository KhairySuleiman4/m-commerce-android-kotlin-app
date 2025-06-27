package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.orders.OrdersRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.entities.LineEntity
import com.example.mcommerce.domain.entities.OrderEntity
import com.example.mcommerce.domain.repoi.OrdersRepo
import kotlinx.coroutines.flow.Flow

class OrdersRepoImpl(
    private val ordersRemoteDataSource: OrdersRemoteDataSource
): OrdersRepo {
    override fun createOrder(
        email: String,
        items: List<LineEntity>,
        address: AddressEntity,
        code: String
    ): Flow<ApiResult<Boolean>> = ordersRemoteDataSource.createOrder(
        email = email,
        items = items,
        address = address,
        code = code
    )

    override suspend fun getOrders(accessToken: String): Flow<ApiResult<List<OrderEntity>>> = ordersRemoteDataSource.getOrders(accessToken)
}