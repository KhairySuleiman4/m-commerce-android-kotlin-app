package com.example.mcommerce.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.mcommerce.data.utils.ConnectivityObserver
import com.example.mcommerce.presentation.addresses.AddressesScreen
import com.example.mcommerce.presentation.auth.SplashScreen
import com.example.mcommerce.presentation.auth.login.LoginScreen
import com.example.mcommerce.presentation.auth.signup.SignupScreen
import com.example.mcommerce.presentation.cart.view.CartScreen
import com.example.mcommerce.presentation.categories.CategoriesScreen
import com.example.mcommerce.presentation.favorites.FavoritesScreen
import com.example.mcommerce.presentation.home.HomeScreen
import com.example.mcommerce.presentation.map.view.MapScreen
import com.example.mcommerce.presentation.navigation.Constants
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.order_details.OrderDetailsScreen
import com.example.mcommerce.presentation.orders.OrdersScreen
import com.example.mcommerce.presentation.personalinfo.PersonalInfoScreen
import com.example.mcommerce.presentation.product_info.ProductInfoScreen
import com.example.mcommerce.presentation.products.ProductsScreen
import com.example.mcommerce.presentation.profile.ProfileScreen
import com.example.mcommerce.presentation.search.SearchScreen
import com.example.mcommerce.presentation.settings.view.SettingsScreen
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.Primary
import com.example.mcommerce.presentation.utils.NoNetworkScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isConnected by connectivityObserver.isConnected.collectAsState()

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.getScreenRoute() ?: ""
            val navDest = remember { mutableIntStateOf(0) }

            val bottomBarRoutes = setOf("home", "categories", "favorite", "profile")
            val topBarRoutes = setOf("home", "categories", "favorite", "profile", "products")

            val showBottomBar = currentRoute in bottomBarRoutes
            val showTopBar = currentRoute in topBarRoutes

            val title = remember {
                mutableStateOf("Pick'n Pay")
            }
            val isUser = remember {
                mutableStateOf(false)
            }

            val showLoginAlert = remember { mutableStateOf(false) }

            Scaffold(
                topBar = {
                    if (showTopBar) {
                        MyAppBar(
                            onSearchClick = {
                                navController.navigate(Screens.SearchScreen)
                            },
                            onCartClick = {
                                if (isUser.value) {
                                    navController.navigate(Screens.Cart)
                                } else {
                                    showLoginAlert.value = true
                                }
                            },
                            title = title.value
                        )
                    }
                },
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavigationBar(
                            navController = navController,
                            currentRoute = navDest.intValue,
                            isGuest = !isUser.value,
                            onShowLoginAlert = { showLoginAlert.value = true }
                        )
                    }
                },
                content = { padding ->
                    NavHostContainer(
                        navController = navController,
                        padding = padding,
                        isConnected = isConnected,
                        changeName = { title.value = it },
                        changeGuest = { isUser.value = it }
                    ) {
                        navDest.intValue = it
                    }
                }
            )
            if (showLoginAlert.value) {
                LoginAlert(
                    onDismissRequest = {
                        showLoginAlert.value = false
                    },
                    onConfirmation = {
                        showLoginAlert.value = false
                        navController.navigate(Screens.Signup) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostContainer(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    isConnected: Boolean,
    navController: NavHostController,
    changeGuest: (Boolean) -> Unit,
    changeName: (String) -> Unit,
    changeRoute: (Int) -> Unit
) {
    NavHost(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues = padding),
        navController = navController,
        startDestination = Screens.Splash,
        builder = {
            composable<Screens.Splash> {
                SplashScreen(navigateTo = {
                    navController.navigate(it) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                )
            }
            composable<Screens.Signup> {
                SignupScreen(navigateToLogin = {
                    navController.navigate(it)
                }) { screen, value ->
                    changeGuest(value)
                    navController.navigate(screen) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            composable<Screens.Login> {
                LoginScreen(navigateToSignup = {
                    navController.navigate(it)
                }
                ) { screen, value ->
                    changeGuest(value)
                    navController.navigate(screen) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            composable<Screens.Home> {
                changeRoute(0)
                changeName("Pick'n Pay")
                if (isConnected) {
                    HomeScreen {
                        navController.navigate(it)
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.Categories> {
                changeRoute(1)
                changeName("Categories")
                if (isConnected) {
                    CategoriesScreen {
                        navController.navigate(it)
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.Favorite> {
                changeRoute(2)
                changeName("Wishlist")
                if (isConnected) {
                    FavoritesScreen {
                        navController.navigate(it)
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.Profile> {
                changeRoute(3)
                changeName("My profile")
                if (isConnected) {
                    ProfileScreen {
                        if (it != Screens.Login)
                            navController.navigate(it)
                        else
                            navController.navigate(it) {
                                popUpTo(0) { inclusive = true }
                            }
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.Settings> {
                changeRoute(3)
                changeName("Settings")
                if (isConnected) {
                    SettingsScreen()
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.Maps> {
                if (isConnected) {
                    MapScreen {
                        navController.popBackStack()
                    }
                } else {
                    NoNetworkScreen()
                }

            }
            composable<Screens.PersonalInfo> {
                if (isConnected) {
                    PersonalInfoScreen()
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.Addresses> {
                if (isConnected) {
                    AddressesScreen {
                        navController.navigate(Screens.Maps)
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.Products> { backStackEntry ->
                val value = backStackEntry.toRoute<Screens.Products>()
                changeName.invoke(value.collectionName)
                if (isConnected) {
                    ProductsScreen(
                        collectionId = value.collectionId,
                    ) {
                        navController.navigate(it)
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.SearchScreen> {
                if (isConnected) {
                    SearchScreen {
                        navController.navigate(it)
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.ProductDetails> { backStackEntry ->
                val value = backStackEntry.toRoute<Screens.ProductDetails>()
                if (isConnected) {
                    ProductInfoScreen(productId = value.productId)
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.Cart> {

                if (isConnected) {
                    CartScreen {
                        navController.navigate(Screens.Home) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.OrdersScreen> {
                if (isConnected) {
                    OrdersScreen {
                        navController.navigate(it)
                    }
                } else {
                    NoNetworkScreen()
                }
            }
            composable<Screens.OrderDetailsScreen> { backStackEntry ->
                val value = backStackEntry.toRoute<Screens.OrderDetailsScreen>()
                if (isConnected) {
                    OrderDetailsScreen(order = value.order){
                        navController.navigate(it)
                    }
                } else {
                    NoNetworkScreen()
                }
            }
        }
    )
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: Int,
    isGuest: Boolean,
    onShowLoginAlert: () -> Unit
) {
    NavigationBar {
        Constants.BottomNavItems.forEachIndexed { index, navItem ->
            NavigationBarItem(
                selected = index == currentRoute,
                onClick = {
                    if (index != currentRoute) {
                        if (index == 2 && isGuest) {
                            onShowLoginAlert()
                        } else {
                            navController.navigate(navItem.route) {
                                launchSingleTop = true
                            }
                        }
                    }

                },
                alwaysShowLabel = true,
                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = "Navigation Icon")
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Primary,
                    selectedTextColor = Color.White,
                    indicatorColor = Primary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    title: String,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
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

@Composable
fun LoginAlert(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = "Hello, Guest!")
        },
        text = {
            Text(text = "You should have an account to be able to access this feature")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
                colors = ButtonColors(
                    containerColor = Background,
                    contentColor = Primary,
                    disabledContainerColor = Background,
                    disabledContentColor = Primary
                )
            ) {
                Text("Sign up")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
                colors = ButtonColors(
                    containerColor = Background,
                    contentColor = Color.Red,
                    disabledContainerColor = Background,
                    disabledContentColor = Primary
                )
            ) {
                Text("cancel")
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
