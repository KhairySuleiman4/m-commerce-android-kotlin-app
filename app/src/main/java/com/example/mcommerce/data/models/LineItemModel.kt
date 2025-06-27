package com.example.mcommerce.data.models

data class LineItemModel(
    val quantity: String,
    val variantTitle: String,
    val productId: String,
    val productTitle: String,
    val price: String,
    val imageUrl: String
)
