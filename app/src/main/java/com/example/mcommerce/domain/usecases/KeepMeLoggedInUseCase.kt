package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.repoi.AuthenticationRepo
import javax.inject.Inject

class KeepMeLoggedInUseCase @Inject constructor(
    private val authRepo: AuthenticationRepo
) {
    suspend operator fun invoke(): Boolean = authRepo.isMeLoggedIn()
}