package com.example.mcommerce.data.models

data class ProductsForSearchModel(
    val id: String,
    val title: String,
    val imageUrl: String,
    val productType: String,
    val price: Double,
    val brand: String
)