package com.example.mcommerce.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mcommerce.presentation.categories.CategoriesScreen
import com.example.mcommerce.presentation.favorites.FavoritesScreen
import com.example.mcommerce.presentation.home.Brands
import com.example.mcommerce.presentation.home.HomeScreen
import com.example.mcommerce.presentation.profile.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                },
                content = { padding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHostContainer(navController = navController, padding = padding)
                    }
                }
            )
        }
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable("home") {
                HomeScreen()
            }
            composable("categories") {
                CategoriesScreen()
            }
            composable("favorite") {
                FavoritesScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    NavigationBar(
        modifier = Modifier.padding(8.dp)
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Constants.BottomNavItems.forEach { navItem ->
            val isSelected = currentDestination?.route == navItem.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(navItem.route)
                },
                alwaysShowLabel = true,
                icon = {
                    Icon(imageVector = navItem.icon, "Navigation Icon")
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Black,
                    selectedTextColor = Color.White,
                    indicatorColor = Color.Black
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TestPreview() {
    Test()
}

@Composable
fun Test() {
    Brands()
}