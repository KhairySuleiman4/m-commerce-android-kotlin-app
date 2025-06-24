package com.example.mcommerce.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class LineItemEntity (
    val quantity: String,
    val variantTitle: String,
    val productId: String,
    val productTitle: String,
    val price: String,
    val imageUrl: String
)