package com.example.mcommerce.data.models


data class CartModel(
    val id: String,
    val checkout: String,
    val subtotalAmount: Double,
    val totalAmount: Double,
    val discountAmount: Double,
    val items: List<LineModel>,
    var code: String = ""
)