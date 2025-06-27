package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import com.example.mcommerce.domain.repoi.AuthenticationRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CreateNewAccountOnFirebaseUseCase @Inject constructor(
    private val authRepo: AuthenticationRepo
) {
    suspend operator fun invoke(credentials: UserCredentialsEntity): Flow<ApiResult<Boolean>> =
        authRepo.createAccountOnFirebase(credentials)
}