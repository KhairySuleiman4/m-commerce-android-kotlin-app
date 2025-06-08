package com.example.mcommerce.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val icon: ImageVector,
    val route:String,
)

object Constants{
    val BottomNavItems = listOf(
        BottomNavItem(
            icon = Icons.Filled.Home,
            route = "home"
        ),
        BottomNavItem(
            icon = Icons.Filled.Menu,
            route = "categories"
        ),
        BottomNavItem(
            icon = Icons.Filled.Favorite,
            route = "favorite"
        ),
        BottomNavItem(
            icon = Icons.Filled.Person,
            route = "profile"
        )
    )
}