package com.example.mcommerce.domain.entities

data class ProductsEntity(
    val id: String,
    val title: String,
    val imageUrl: String,
    val productType: String,
    val price: String
)
