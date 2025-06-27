package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.repoi.AuthenticationRepo
import javax.inject.Inject

class GetUserAccessTokenUseCase @Inject constructor(
    private val repo: AuthenticationRepo
) {
    suspend operator fun invoke(): String = repo.getCustomerAccessToken()
}