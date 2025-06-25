package com.example.mcommerce.presentation.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.R
import com.example.mcommerce.presentation.auth.AuthContract
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.theme.Primary


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToSignup: (Screens) -> Unit,
    navigateToHome: (Screens, Boolean) -> Unit,
) {
    val event = viewModel.events.value
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        when (event) {
            is AuthContract.Events.Idle -> {}
            is AuthContract.Events.NavigateToHomeGuest -> {
                navigateToHome(Screens.Home, false)
                viewModel.resetEvent()
            }

            AuthContract.Events.NavigateToHomeUser -> {
                navigateToHome(Screens.Home, true)
                viewModel.resetEvent()
            }

            is AuthContract.Events.NavigateToLogin -> {}
            is AuthContract.Events.NavigateToSignup -> {
                navigateToSignup(Screens.Signup)
                viewModel.resetEvent()
            }

            is AuthContract.Events.ShowLoading -> {
                isLoading.value = true
            }

            is AuthContract.Events.ShowSnackbar -> {
                snackbarHostState.showSnackbar(message = event.message)
                isLoading.value = false
                viewModel.resetEvent()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background,
        modifier = modifier
    ) { paddingValues ->
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LoginComposable(
                onLoginClicked = { email, password ->
                    viewModel.invokeActions(
                        AuthContract.LoginAction.ClickOnLoginButton(
                            email,
                            password
                        )
                    )
                },
                onSignupClicked = {
                    viewModel.invokeActions(AuthContract.LoginAction.ClickOnNavigateToSignup)
                },
                onGuestClicked = {
                    viewModel.invokeActions(AuthContract.LoginAction.ClickOnContinueAsGuest)
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun LoginComposable(
    onLoginClicked: (String, String) -> Unit,
    onSignupClicked: () -> Unit,
    onGuestClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            LoginScreenHeader()
        }

        item {
            EmailSection(email = email.value) {
                email.value = it
            }
        }

        item {
            PasswordSection(password = password.value) {
                password.value = it
            }
        }

        item {
            LoginButton(
                onLoginClicked = { email, password ->
                    onLoginClicked(email, password)
                },
                email = email.value,
                password = password.value
            )
        }

        item {
            DontHaveAnAccountSection(onSignupClicked)
        }

        item {
            ContinueAsGuestSection(onGuestClicked)
        }
    }
}

@Composable
fun LoginScreenHeader(modifier: Modifier = Modifier) {
    Text(
        fontFamily = PoppinsFontFamily,
        text = stringResource(R.string.login),
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    )

    Text(
        fontFamily = PoppinsFontFamily,
        modifier = modifier.padding(top = 8.dp),
        text = stringResource(R.string.welcome_back),
        color = Color.Gray,
        fontSize = 18.sp
    )
}

@Composable
fun EmailSection(modifier: Modifier = Modifier, email: String, onMailChanged: (String) -> Unit) {
    Column {
        Text(
            fontFamily = PoppinsFontFamily,
            modifier = modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp
                ),
            text = stringResource(R.string.email),
            fontSize = 18.sp
        )

        OutlinedTextField(
            value = email,
            onValueChange = onMailChanged,
            modifier = modifier
                .padding(
                    top = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun PasswordSection(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChanged: (String) -> Unit
) {
    val isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            fontFamily = PoppinsFontFamily,
            modifier = modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp
                ),
            text = stringResource(R.string.password),
            fontSize = 18.sp
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            modifier = modifier
                .padding(
                    top = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
    }
}

@Composable
fun LoginButton(
    onLoginClicked: (String, String) -> Unit,
    email: String,
    password: String,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 32.dp,
                start = 16.dp,
                end = 16.dp
            )
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        onClick = {
            onLoginClicked(email, password)
        }
    )
    {
        Text(
            fontFamily = PoppinsFontFamily,
            text = stringResource(R.string.login),
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
fun DontHaveAnAccountSection(
    onSignupClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 32.dp)
    ) {
        Text(
            fontFamily = PoppinsFontFamily,
            text = stringResource(R.string.dont_have_an_accout)
        )

        Text(
            fontFamily = PoppinsFontFamily,
            modifier = modifier
                .padding(start = 4.dp)
                .clickable {
                    onSignupClicked()
                },
            text = stringResource(R.string.sign_up),
            textDecoration = TextDecoration.Underline,
            color = Primary
        )
    }
}

@Composable
fun ContinueAsGuestSection(
    onGuestClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 8.dp)
    ) {
        Text(
            fontFamily = PoppinsFontFamily,
            text = stringResource(R.string.continue_str)
        )

        Text(
            fontFamily = PoppinsFontFamily,
            modifier = modifier
                .padding(start = 4.dp)
                .clickable {
                    onGuestClicked()
                },
            text = stringResource(R.string.as_guest),
            textDecoration = TextDecoration.Underline,
            color = Primary
        )
    }
}