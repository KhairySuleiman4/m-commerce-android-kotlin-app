package com.example.mcommerce.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mcommerce.R
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
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val bottomBarRoutes = setOf(
                "com.example.mcommerce.presentation.navigation.Screens.Home",
                "com.example.mcommerce.presentation.navigation.Screens.Categories",
                "com.example.mcommerce.presentation.navigation.Screens.Favorite",
                "com.example.mcommerce.presentation.navigation.Screens.Profile"
            )

            val showBottomBar = currentRoute in bottomBarRoutes

            Scaffold(
                topBar = {
                    MyAppBar(
                        onSearchClick = {
                            //navController.navigate(Screens.SearchScreen)
                        },
                        onCartClick = {
                            //navController.navigate(Screens.CartScreen)
                        }
                    )
                },
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavigationBar(navController = navController, currentRoute = currentRoute)
                    }
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
                CategoriesScreen(navController = navController)
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
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
    ) {

    NavigationBar(
        modifier = Modifier.padding(8.dp)
    ) {
        Constants.BottomNavItems.forEachIndexed { index, navItem ->
            val isSelected = when (currentRoute) {
                "com.example.mcommerce.presentation.navigation.Screens.Home" -> index == 0
                "com.example.mcommerce.presentation.navigation.Screens.Categories" -> index == 1
                "com.example.mcommerce.presentation.navigation.Screens.Favorite" -> index == 2
                "com.example.mcommerce.presentation.navigation.Screens.Profile" -> index == 3
                else -> false
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(navItem.route){
                        popUpTo(navController.graph.startDestinationId){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = true,
                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = "Navigation Icon")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Shopping App",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Search",
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(onClick = onCartClick) {
                Icon(
                    painter = painterResource(id = R.drawable.cart_icon),
                    contentDescription = "Shopping Cart",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    )
}