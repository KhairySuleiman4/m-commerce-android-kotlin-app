package com.example.mcommerce.data.models

data class OrderModel(
    val name: String,
    val processedAt: String,
    val subtotalPrice: String,
    val totalPrice: String,
    val shippingAddress: String,
    val city: String,
    val customerName: String,
    val phone: String,
    val lineItems: List<LineItemModel>
)