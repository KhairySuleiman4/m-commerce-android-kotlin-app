package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.repoi.CurrencyRepo
import javax.inject.Inject

class GetCurrentExchangeRateUseCase @Inject constructor(
    private val currencyRepo: CurrencyRepo
) {
    suspend operator fun invoke(): Double = currencyRepo.getExchangeValueFromLocalSource()
}