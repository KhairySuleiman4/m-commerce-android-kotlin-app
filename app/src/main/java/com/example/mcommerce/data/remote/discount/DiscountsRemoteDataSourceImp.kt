package com.example.mcommerce.data.remote.discount

import com.example.mcommerce.data.remote.admingaraphgl.AdminGraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import kotlinx.coroutines.flow.Flow

class DiscountsRemoteDataSourceImp(
    private val adminGraphQLService: AdminGraphQLService
): DiscountsRemoteDataSource{
    override fun getDiscountCodes(): Flow<ApiResult<List<String?>?>> = executeAPI{
        adminGraphQLService.getDiscountCodes().data?.codeDiscountNodes?.nodes?.map { it.codeDiscount.onDiscountCodeBasic?.title }
    }

}