package com.example.mcommerce.domain.usecases


import com.example.mcommerce.domain.repoi.CartRepo
import javax.inject.Inject

class ClearLocalCartUseCase @Inject constructor(
    private val cartRepo: CartRepo
) {
    operator fun invoke(): Boolean = cartRepo.clearLocalCart()
}