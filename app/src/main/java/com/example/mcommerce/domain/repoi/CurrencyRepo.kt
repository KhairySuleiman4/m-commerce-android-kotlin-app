package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

interface CurrencyRepo {
    suspend fun getCurrencyFromRemoteSource(): Flow<ApiResult<ExchangeRateEntity?>>
    suspend fun getCurrencyValueFromLocalSource(): String
    suspend fun getExchangeValueFromLocalSource(): Double
    suspend fun setCurrencyValueIntoLocalSource(value: String)
    suspend fun setExchangeValueIntoLocalSource(value: Double)
}