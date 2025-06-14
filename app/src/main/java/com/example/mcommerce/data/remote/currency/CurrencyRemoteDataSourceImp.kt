package com.example.mcommerce.data.remote.currency

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.remote.exchangerateapi.ExchangeService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

class CurrencyRemoteDataSourceImp(private val service:ExchangeService): CurrencyRemoteDataSource {
    override suspend fun getCurrency(): Flow<ApiResult<ExchangeRateEntity?>>
    = executeAPI { service.getEGPExchangeRate().body()?.toEntity() }
}