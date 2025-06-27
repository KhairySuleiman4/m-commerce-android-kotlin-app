package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.repoi.CurrencyRepo
import javax.inject.Inject

class GetCurrentCurrencyUseCase @Inject constructor(
    private val currencyRepo: CurrencyRepo
) {
    suspend operator fun invoke(): String = currencyRepo.getCurrencyValueFromLocalSource()
}