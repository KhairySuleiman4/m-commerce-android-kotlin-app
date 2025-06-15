package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.repoi.AuthenticationRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KeepMeLoggedInUseCase @Inject constructor(
    private val authRepo: AuthenticationRepo
) {
    suspend operator fun invoke(): Flow<ApiResult<Boolean>> = authRepo.isMeLoggedIn()
}