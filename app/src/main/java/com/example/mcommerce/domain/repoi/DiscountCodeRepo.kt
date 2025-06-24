package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import kotlinx.coroutines.flow.Flow

interface DiscountCodeRepo {
    fun getCodes(): Flow<ApiResult<List<String?>?>>
}