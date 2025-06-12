package com.example.mcommerce.data.remote.currency

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

interface CurrencyRemoteDataSource {
    suspend fun getCurrency(): Flow<ApiResult<ExchangeRateEntity?>>

}