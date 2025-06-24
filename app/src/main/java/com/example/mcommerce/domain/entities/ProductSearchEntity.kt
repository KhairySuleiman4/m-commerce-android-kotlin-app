package com.example.mcommerce.domain.entities

data class ProductSearchEntity(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val productType: String = "",
    val price: Double = 0.0,
    val brand: String = "",
    val isFavorite: Boolean = false
)