package com.example.mcommerce.presentation.auth.view

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcommerce.R
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun SignupScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            SignupScreenHeader()
        }

        item {
            NameSection()
        }

        item {
            EmailSection()
        }

        item {
            PhoneSection()
        }

        item {
            PasswordSection()
        }

        item {
            ConfirmPasswordSection()
        }

        item {
            SignupButton()
        }

        item{
            SignupWithSection()
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
fun SignupScreenHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.create_account),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.fit_your_information),
            color = Color.Gray,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NameSection(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    Column {
        Text(
            modifier = modifier
                .padding(
                    top = 24.dp,
                    start = 16.dp
                ),
            fontWeight = FontWeight.Bold,
            text = stringResource(R.string.name),
            fontSize = 18.sp
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = modifier
                .padding(
                    top = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.enter_your_name),
                    color = Color.Gray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun PhoneSection(modifier: Modifier = Modifier) {
    var phone by remember { mutableStateOf("") }
    Column {
        Text(
            modifier = modifier
                .padding(
                    top = 24.dp,
                    start = 16.dp
                ),
            fontWeight = FontWeight.Bold,
            text = stringResource(R.string.phone),
            fontSize = 18.sp
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            modifier = modifier
                .padding(
                    top = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.enter_your_phone),
                    color = Color.Gray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        )
    }
}

@Composable
fun ConfirmPasswordSection(modifier: Modifier = Modifier) {
    var password by remember { mutableStateOf("") }
    val isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            modifier = modifier
                .padding(
                    top = 24.dp,
                    start = 16.dp
                ),
            fontWeight = FontWeight.Bold,
            text = stringResource(R.string.confirm_password),
            fontSize = 18.sp
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = modifier
                .padding(
                    top = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.enter_your_password_again),
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
fun SignupButton(modifier: Modifier = Modifier) {
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
            // signup logic
        }
    )
    {
        Text(
            text = stringResource(R.string.sign_up),
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
fun SignupWithSection(modifier: Modifier = Modifier) {
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
            text = stringResource(R.string.or_sign_up_with)
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

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SignupScreenPreview() {
    SignupScreen()
}