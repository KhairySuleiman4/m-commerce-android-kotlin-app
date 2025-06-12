package com.example.mcommerce.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcommerce.R
import com.example.mcommerce.presentation.theme.Primary


@Composable
fun LoginScreen(modifier: Modifier = Modifier) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            LoginScreenHeader()
        }

        item {
            //EmailSection()
        }

        item {
            //PasswordSection()
        }

        item {
            LoginButton()
        }

        item{
            LoginWithSection()
        }

        item {
            DontHaveAnAccountSection()
        }

        item {
            ContinueAsGuestSection()
        }
    }
}

@Composable
fun LoginScreenHeader(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.login),
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )

    Text(
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
            modifier = modifier
                .padding(
                    top = 24.dp,
                    start = 16.dp
                ),
            fontWeight = FontWeight.Bold,
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
            placeholder = {
                Text(
                    stringResource(R.string.enter_your_email),
                    color = Color.Gray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun PasswordSection(modifier: Modifier = Modifier, password: String, onPasswordChanged: (String) -> Unit) {
    val isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            modifier = modifier
                .padding(
                    top = 24.dp,
                    start = 16.dp
                ),
            fontWeight = FontWeight.Bold,
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
            placeholder = {
                Text(
                    stringResource(R.string.enter_your_password),
                    color = Color.Gray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
    }
}

@Composable
fun LoginButton(modifier: Modifier = Modifier) {
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
            // login logic
        }
    )
    {
        Text(
            text = stringResource(R.string.login),
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
fun LoginWithSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(
                top = 32.dp
            )
    ) {
        HorizontalDivider(
            modifier = modifier
                .padding(8.dp)
                .width(64.dp)
                .align(Alignment.CenterVertically),
            color = Color.Black,
            thickness = 1.dp
        )

        Text(
            text = stringResource(R.string.or_login_with)
        )

        HorizontalDivider(
            modifier = modifier
                .padding(8.dp)
                .width(64.dp)
                .align(Alignment.CenterVertically),
            color = Color.Black,
            thickness = 1.dp
        )
    }

    Image(
        modifier = modifier
            .padding(top = 32.dp)
            .size(32.dp)
            .clip(CircleShape)
            .border(0.5.dp, Color.Gray, CircleShape)
            .padding(8.dp),
        painter = painterResource(R.drawable.google),
        contentDescription = stringResource(R.string.google_icon)
    )
}

@Composable
fun DontHaveAnAccountSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(top = 32.dp)
    ){
        Text(
            text = stringResource(R.string.dont_have_an_accout)
        )

        Text(
            modifier = modifier.padding(start = 8.dp),
            text = stringResource(R.string.sign_up),
            textDecoration = TextDecoration.Underline,
            color = Primary
        )
    }
}

@Composable
fun ContinueAsGuestSection(modifier: Modifier = Modifier) {
    Row (
        modifier = modifier.padding(top = 8.dp)
    ){
        Text(
            text = stringResource(R.string.continue_str)
        )

        Text(
            modifier = modifier.padding(start = 4.dp),
            text = stringResource(R.string.as_guest),
            textDecoration = TextDecoration.Underline,
            color = Primary
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SignupScreen() {
    LoginScreen()
}