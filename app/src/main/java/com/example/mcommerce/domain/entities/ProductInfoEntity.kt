package com.example.mcommerce.domain.entities

data class ProductInfoEntity(
    val id: String,
    val images: List<String>,
    val title: String,
    val price: Double,
    val priceUnit: String,
    val productType: String,
    val vendor: String,
    val description: String,
    val variants: List<ProductVariantEntity>,
    val isFavorite: Boolean
)