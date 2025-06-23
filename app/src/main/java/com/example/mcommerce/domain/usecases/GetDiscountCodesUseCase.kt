package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.repoi.DiscountCodeRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDiscountCodesUseCase @Inject constructor(
    private val discountCodeRepo: DiscountCodeRepo
) {
    operator fun invoke(): Flow<ApiResult<List<String?>?>> = discountCodeRepo.getCodes()
}