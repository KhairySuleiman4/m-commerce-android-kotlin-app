package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CartEntity
import com.example.mcommerce.domain.repoi.CartRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddDiscountToCartUseCase @Inject constructor(
    private val cartRepo: CartRepo
) {
    operator fun invoke(code: String): Flow<ApiResult<CartEntity?>> = cartRepo.addDiscountCode(code)
}