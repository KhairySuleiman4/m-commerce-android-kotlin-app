package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.repoi.CartRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClearOnlineCartUseCase @Inject constructor(
    private val cartRepo: CartRepo
){
   suspend operator fun invoke() : Flow<ApiResult<Boolean>> = cartRepo.removeCart()
}