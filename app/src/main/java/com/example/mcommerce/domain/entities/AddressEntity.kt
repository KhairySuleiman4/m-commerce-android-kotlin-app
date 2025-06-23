package com.example.mcommerce.domain.entities

data class AddressEntity(
    val id: String,
    var name: String,
    var subName: String,
    var country: String,
    var city: String,
    var zip: String,
    val latitude: Double,
    val longitude: Double,
    var isDefault: Boolean = false
)