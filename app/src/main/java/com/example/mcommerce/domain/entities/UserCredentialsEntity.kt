package com.example.mcommerce.domain.entities

data class UserCredentialsEntity(
    val name: String,
    val mail: String,
    val phoneNumber: String,
    val password: String,
    val isVerified: Boolean,
    val accessToken: String
)