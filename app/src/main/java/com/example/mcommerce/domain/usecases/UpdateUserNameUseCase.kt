package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.repoi.AuthenticationRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val authRepo: AuthenticationRepo
) {
    operator fun invoke(name: String): Flow<ApiResult<String>> = authRepo.updateNameOnAccount(name)
}