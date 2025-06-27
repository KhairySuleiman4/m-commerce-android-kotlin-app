package com.example.mcommerce.domain.entities


data class CartEntity(
    val id: String,
    val checkout: String,
    val subtotalAmount: Double,
    val totalAmount: Double,
    val discountAmount: Double,
    val items: List<LineEntity>,
    var code: String = ""
)
