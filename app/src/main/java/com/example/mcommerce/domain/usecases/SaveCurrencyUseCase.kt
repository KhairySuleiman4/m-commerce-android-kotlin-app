package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import com.example.mcommerce.domain.repoi.CurrencyRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveCurrencyUseCase @Inject constructor(private val currencyRepo:CurrencyRepo) {
    suspend fun getRates(): Flow<ApiResult<ExchangeRateEntity?>> = currencyRepo.getCurrencyFromRemoteSource()

    suspend fun getCurrentCurrency(): String = currencyRepo.getCurrencyValueFromLocalSource()

    suspend fun saveCurrency(value: String) = currencyRepo.setCurrencyValueIntoLocalSource(value)

    suspend fun saveExchangeRate(value: Double) = currencyRepo.setExchangeValueIntoLocalSource(value)
}