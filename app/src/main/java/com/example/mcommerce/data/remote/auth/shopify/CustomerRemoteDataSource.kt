package com.example.mcommerce.data.remote.auth.shopify

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CustomerEntity
import kotlinx.coroutines.flow.Flow

interface CustomerRemoteDataSource {
    suspend fun createCustomer(customer: CustomerEntity): Flow<ApiResult<String>>
    suspend fun getAccessToken(customer: CustomerEntity): Flow<ApiResult<String>>
}