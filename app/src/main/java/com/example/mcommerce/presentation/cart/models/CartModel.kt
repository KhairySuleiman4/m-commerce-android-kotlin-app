package com.example.mcommerce.presentation.cart.models

data class CartModel(
    val subtotal: Double,
    val total: Double,
    val lines: List<LineItem>
)

