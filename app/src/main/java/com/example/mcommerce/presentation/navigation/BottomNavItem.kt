package com.example.mcommerce.presentation.navigation

import com.example.mcommerce.R

data class BottomNavItem(
    val icon: Int,
    val route: Screens,
)

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            icon = R.drawable.home,
            route = Screens.Home
        ),
        BottomNavItem(
            icon = R.drawable.categories,
            route = Screens.Categories
        ),
        BottomNavItem(
            icon = R.drawable.favorites,
            route = Screens.Favorite
        ),
        BottomNavItem(
            icon = R.drawable.profile,
            route = Screens.Profile
        )
    )
}