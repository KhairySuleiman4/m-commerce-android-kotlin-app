package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.repoi.AuthenticationRepo
import javax.inject.Inject

class IsGuestModeUseCase @Inject constructor(
    private val repo: AuthenticationRepo
) {
    operator fun invoke(): Boolean = repo.isGuestMode()
}