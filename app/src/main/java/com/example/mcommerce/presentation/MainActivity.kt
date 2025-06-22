package com.example.mcommerce.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mcommerce.R
import com.example.mcommerce.presentation.addresses.AddressesScreen
import com.example.mcommerce.presentation.auth.login.LoginScreen
import com.example.mcommerce.presentation.auth.signup.SignupScreen
import com.example.mcommerce.presentation.cart.view.CartScreen
import com.example.mcommerce.presentation.categories.CategoriesScreen
import com.example.mcommerce.presentation.favorites.FavoritesScreen
import com.example.mcommerce.presentation.home.HomeScreen
import com.example.mcommerce.presentation.map.view.MapScreen
import com.example.mcommerce.presentation.navigation.Constants
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.personalinfo.PersonalInfoScreen
import com.example.mcommerce.presentation.product_info.ProductInfoScreen
import com.example.mcommerce.presentation.products.ProductsScreen
import com.example.mcommerce.presentation.profile.ProfileScreen
import com.example.mcommerce.presentation.search.SearchScreen
import com.example.mcommerce.presentation.settings.view.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.getScreenRoute() ?: ""
            val navDest = remember { mutableIntStateOf(0) }

            val bottomBarRoutes = setOf("home", "categories", "favorite", "profile")
            val topBarRoutes = setOf("home", "categories", "favorite", "profile", "products")

            val showBottomBar = currentRoute in bottomBarRoutes
            val showTopBar = currentRoute in topBarRoutes

            Scaffold(
                topBar = {
                    if(showTopBar){
                        MyAppBar(
                            onSearchClick = {
                                navController.navigate(Screens.SearchScreen)
                            },
                            onCartClick = {
                                navController.navigate(Screens.Cart)
                            }
                        )
                    }
                },
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavigationBar(
                            navController = navController,
                            currentRoute = navDest.intValue,
                        )
                    }
                },
                content = { padding ->
                    NavHostContainer(
                        navController = navController,
                        padding = padding
                    ){
                        navDest.intValue = it
                    }
                }
            )
        }
    }
}

@Composable
fun NavHostContainer(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navController: NavHostController,
    changeRoute: (Int) -> Unit,
) {
    NavHost(
        modifier = modifier.fillMaxSize().padding(paddingValues = padding),
        navController = navController,
        startDestination = Screens.Signup,
        builder = {
            composable<Screens.Signup> {
                SignupScreen(navigateToLogin = {
                    navController.navigate(it)
                }){
                    navController.navigate(it){
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            composable<Screens.Login> {
                LoginScreen(navigateToSignup = {
                    navController.navigate(it)
                }
                ){
                    navController.navigate(it){
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            composable<Screens.Home> {
                changeRoute(0)
                HomeScreen{
                    navController.navigate(it)
                }
            }
            composable<Screens.Categories> {
                changeRoute(1)
                CategoriesScreen{
                    navController.navigate(it)
                }
            }
            composable<Screens.Favorite> {
                changeRoute(2)
                FavoritesScreen{
                    navController.navigate(it)
                }
            }
            composable<Screens.Profile> {
                changeRoute(3)
                ProfileScreen{
                    if (it != Screens.Login)
                        navController.navigate(it)
                    else
                        navController.navigate(it){
                            popUpTo(0) { inclusive = true }
                        }
                }
            }
            composable<Screens.Settings> {
                changeRoute(3)
                SettingsScreen()
            }
            composable<Screens.Maps> {
                MapScreen()
            }
            composable<Screens.PersonalInfo> {
                PersonalInfoScreen()
            }
            composable<Screens.Addresses> {
                AddressesScreen {
                    navController.navigate(Screens.Maps)
                }
            }
            composable<Screens.Products>{ backStackEntry ->
                val value = backStackEntry.toRoute<Screens.Products>()
                ProductsScreen(
                    collectionId = value.brandId
                ){
                    navController.navigate(it)
                }
            }
            composable<Screens.SearchScreen> {
                SearchScreen{
                    navController.navigate(it)
                }
            }
            composable<Screens.ProductDetails> { backStackEntry ->
                val value = backStackEntry.toRoute<Screens.ProductDetails>()
                ProductInfoScreen(productId = value.productId)
            }
            composable<Screens.Cart> {
                CartScreen()
            }
        }
    )
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: Int,
    ) {
    NavigationBar{
        Constants.BottomNavItems.forEachIndexed { index, navItem ->
            NavigationBarItem(
                selected = index==currentRoute,
                onClick = {
                    if (index != currentRoute)
                    navController.navigate(navItem.route){
                        launchSingleTop = true
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

fun NavBackStackEntry.getScreenRoute(): String {
    val destinationName = destination.route
    return when {
        destinationName?.contains("Home") == true -> "home"
        destinationName?.contains("Categories") == true -> "categories"
        destinationName?.contains("Favorite") == true -> "favorite"
        destinationName?.contains("Profile") == true -> "profile"
        destinationName?.contains("Products") == true -> "products"
        else -> ""
    }
}
