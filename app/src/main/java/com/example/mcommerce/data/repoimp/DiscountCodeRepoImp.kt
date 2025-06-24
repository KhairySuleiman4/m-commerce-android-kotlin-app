package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.discount.DiscountsRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.repoi.DiscountCodeRepo
import kotlinx.coroutines.flow.Flow

class DiscountCodeRepoImp(
    private val discountsRemoteDataSource: DiscountsRemoteDataSource
): DiscountCodeRepo{
    override fun getCodes(): Flow<ApiResult<List<String?>?>> = discountsRemoteDataSource.getDiscountCodes()
}