package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.datastore.ExchangeDataStore
import com.example.mcommerce.data.remote.currency.CurrencyRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import com.example.mcommerce.domain.repoi.CurrencyRepo
import kotlinx.coroutines.flow.Flow

class CurrencyRepoImp(
    private val local: ExchangeDataStore,
    private val remote: CurrencyRemoteDataSource
) : CurrencyRepo {

    override suspend fun getCurrencyFromRemoteSource(): Flow<ApiResult<ExchangeRateEntity?>> =
        remote.getCurrency()

    override suspend fun getCurrencyValueFromLocalSource(): String = local.getCurrency()

    override suspend fun getExchangeValueFromLocalSource(): Double = local.getExchange()

    override suspend fun setCurrencyValueIntoLocalSource(value: String) = local.setCurrency(value)

    override suspend fun setExchangeValueIntoLocalSource(value: Double) = local.setExchange(value)
}