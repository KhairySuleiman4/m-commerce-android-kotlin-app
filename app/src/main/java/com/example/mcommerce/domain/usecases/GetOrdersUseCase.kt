package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.OrderEntity
import com.example.mcommerce.domain.repoi.OrdersRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val ordersRepo: OrdersRepo
) {
    suspend operator fun invoke(accessToken: String): Flow<ApiResult<List<OrderEntity>>> = ordersRepo.getOrders(accessToken)
}