package com.example.mcommerce.domain.entities

data class AddressEntity(
    val name: String,
    val country: String,
    val zip: String,
    val latitude: Double,
    val longitude: Double
)