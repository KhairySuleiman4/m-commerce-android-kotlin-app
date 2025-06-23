package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.repoi.AuthenticationRepo
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    private val authRepo: AuthenticationRepo
) {
    operator fun invoke(): String = authRepo.getUserName()
}