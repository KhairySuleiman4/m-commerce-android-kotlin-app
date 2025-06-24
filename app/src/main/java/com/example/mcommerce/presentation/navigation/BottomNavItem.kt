package com.example.mcommerce.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val icon: ImageVector,
    val route: Screens,
)

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            icon = Icons.Filled.Home,
            route = Screens.Home
        ),
        BottomNavItem(
            icon = Icons.Filled.Menu,
            route = Screens.Categories
        ),
        BottomNavItem(
            icon = Icons.Filled.Favorite,
            route = Screens.Favorite
        ),
        BottomNavItem(
            icon = Icons.Filled.Person,
            route = Screens.Profile
        )
    )
}