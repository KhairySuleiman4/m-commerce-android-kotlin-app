package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.repoi.AuthenticationRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsGuestModeUseCase @Inject constructor(
    private val repo: AuthenticationRepo
) {
    operator fun invoke(): Flow<ApiResult<Boolean>> = repo.isGuestMode()
}