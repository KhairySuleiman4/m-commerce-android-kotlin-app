package com.example.mcommerce.data.models


data class AddressModel(
    val id: String,
    var name: String,
    var subName: String,
    val country: String,
    val city: String,
    val zip: String,
    val latitude: Double,
    val longitude: Double,
    var isDefault: Boolean = false,
    var customerName: String = ""
    )
