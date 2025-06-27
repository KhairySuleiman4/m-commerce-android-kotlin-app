package com.example.mcommerce.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.R
import com.example.mcommerce.presentation.auth.signup.SignupViewModel
import com.example.mcommerce.presentation.navigation.Screens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    navigateTo: (Screens) -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        if (viewModel.keepMeLoggedIn()) {
            navigateTo(Screens.Home)
        } else {
            navigateTo(Screens.Signup)
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "App Logo",
            modifier = Modifier.size(160.dp)
        )
    }
}
