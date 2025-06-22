package com.example.mcommerce.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object Signup: Screens()
    @Serializable
    data object Login: Screens()
    @Serializable
    data object Home : Screens()
    @Serializable
    data object Categories : Screens()
    @Serializable
    data object Favorite : Screens()
    @Serializable
    data object Profile : Screens()
    @Serializable
    data object Settings : Screens()
    @Serializable
    data object Maps : Screens()
    @Serializable
    data object Cart : Screens()
    @Serializable
    data object PersonalInfo : Screens()
    @Serializable
    data object Addresses : Screens()
    @Serializable
    data class Products(val brandId: String, val brandName: String = "") : Screens()
    @Serializable
    data class ProductDetails(val productId: String): Screens()
    @Serializable
    data object SearchScreen: Screens()
    @Serializable
    data object OrdersScreen: Screens()
    @Serializable
    data class OrderDetailsScreen(val orderId: String): Screens()
}
