package com.example.mcommerce.presentation.cart.models

data class LineItem(
    val name: String,
    val brand: String,
    val size: String,
    val quantity: Int,
    val price: Double,
)
