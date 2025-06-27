package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.entities.LineEntity
import com.example.mcommerce.domain.repoi.OrdersRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateOrderUseCase  @Inject constructor(
    private val ordersRepo: OrdersRepo
) {
    operator fun invoke(
        email: String,
        items: List<LineEntity>,
        address: AddressEntity,
        code: String
    ): Flow<ApiResult<Boolean>> = ordersRepo.createOrder(email, items, address, code)
}