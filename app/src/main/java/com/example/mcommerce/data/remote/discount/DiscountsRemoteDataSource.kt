package com.example.mcommerce.data.remote.discount

import com.example.mcommerce.domain.ApiResult
import kotlinx.coroutines.flow.Flow

interface DiscountsRemoteDataSource {
    fun getDiscountCodes(): Flow<ApiResult<List<String?>?>>
}