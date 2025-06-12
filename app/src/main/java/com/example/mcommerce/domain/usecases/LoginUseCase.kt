package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.repoi.AuthenticationRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepo: AuthenticationRepo
) {
    suspend operator fun invoke(email: String, password: String): Flow<ApiResult<Boolean>> = authRepo.login(email, password)
}