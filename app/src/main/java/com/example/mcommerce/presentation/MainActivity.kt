package com.example.mcommerce.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mcommerce.presentation.categories.CategoriesScreen
import com.example.mcommerce.presentation.favorites.FavoritesScreen
import com.example.mcommerce.presentation.home.HomeScreen
import com.example.mcommerce.presentation.navigation.Constants
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.productdetails.ProductInfoScreen
import com.example.mcommerce.presentation.products.ProductsScreen
import com.example.mcommerce.presentation.profile.ProfileScreen
import com.example.mcommerce.presentation.settings.view.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        startDestination = Screens.Home,
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable<Screens.Home> {
                HomeScreen(navController = navController)
            }
            composable<Screens.Categories> {
                CategoriesScreen()
            }
            composable<Screens.Favorite> {
                FavoritesScreen()
            }
            composable<Screens.Profile> {
                ProfileScreen(navController = navController)
            }
            composable<Screens.Settings> {
                SettingsScreen()
            }
            composable<Screens.Products>{ backStackEntry ->
                val value = backStackEntry.toRoute<Screens.Products>()
                ProductsScreen(
                    navController = navController,
                    brandId = value.brandId,
                    brandName = value.brandName
                )
            }
            composable<Screens.ProductDetails> { backStackEntry ->
                val value = backStackEntry.toRoute<Screens.ProductDetails>()
                ProductInfoScreen(productId = value.productId)
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    NavigationBar(
        modifier = Modifier.padding(8.dp)
    ) {
        val currentDestination = remember { mutableIntStateOf(0) }

        Constants.BottomNavItems.forEachIndexed { index, navItem ->
            val isSelected = currentDestination.intValue == index

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    currentDestination.intValue = index
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