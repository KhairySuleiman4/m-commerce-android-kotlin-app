package com.example.mcommerce.data.models

data class LineModel(
    val id: String,
    val quantity: Int,
    val price: Double,
    val image: String,
    val title: String,
    val category: String,
    val brand: String,
    val lineId: String,
    val productId: String
)