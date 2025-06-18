package com.example.mcommerce.domain.entities

data class ProductVariantEntity(
    val id: String,
    val imageUrl: String,
    val title: String,
    val price: String,
    var isSelected: Boolean = false
)