package com.example.mcommerce.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class OrderEntity(
    val name: String,
    val processedAt: String,
    val subtotalPrice: String,
    val totalPrice: String,
    val shippingAddress: String,
    val city: String,
    val customerName: String,
    val phone: String,
    val lineItems: List<LineItemEntity>
)
