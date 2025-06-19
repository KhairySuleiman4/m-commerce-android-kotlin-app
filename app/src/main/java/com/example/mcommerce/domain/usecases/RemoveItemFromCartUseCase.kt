package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CartEntity
import com.example.mcommerce.domain.repoi.CartRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveItemFromCartUseCase @Inject constructor(
    private val cartRepo: CartRepo
) {
    operator fun invoke(itemId: String): Flow<ApiResult<CartEntity?>> = cartRepo.removeItemFromCart(itemId)
}