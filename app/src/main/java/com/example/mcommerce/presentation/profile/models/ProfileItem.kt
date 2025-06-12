package com.example.mcommerce.presentation.profile.models

import com.example.mcommerce.presentation.navigation.Screens

data class ProfileItem(
    val image: Int,
    val text: String,
    val route: Screens
)