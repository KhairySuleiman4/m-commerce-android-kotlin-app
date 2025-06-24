package com.example.mcommerce.presentation.personalinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun PersonalInfoScreen(
    modifier: Modifier = Modifier,
    viewModel: PersonalInfoViewModel = hiltViewModel()
) {
    val email = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val event = viewModel.events.value

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    LaunchedEffect(event) {
        when (event) {
            PersonalInfoContract.Event.Idle -> {

            }

            is PersonalInfoContract.Event.SaveDone -> {
                snackbarHostState.showSnackbar(
                    "The new Name ${event.name} have been saved!",
                    duration = SnackbarDuration.Short
                )
            }

            is PersonalInfoContract.Event.ShowError -> {
                snackbarHostState.showSnackbar(event.msg, duration = SnackbarDuration.Short)
            }

            is PersonalInfoContract.Event.UpdateData -> {
                email.value = event.email
                name.value = event.name
            }
        }
    }

    PersonalInfoPage(
        modifier = modifier,
        name = name.value,
        email = email.value,
        snackbarHostState = snackbarHostState
    ) {
        viewModel.invokeActions(PersonalInfoContract.Action.ClickOnSave(it))
    }
}

@Composable
fun PersonalInfoPage(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    snackbarHostState: SnackbarHostState,
    changeNameAction: (String) -> Unit
) {
    val textFieldValue = remember { mutableStateOf(name) }
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (textFieldValue.value != name && textFieldValue.value.length > 4)
                        changeNameAction(textFieldValue.value)
                },
                containerColor = Primary,
                contentColor = Background,
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Submit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    modifier = modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    fontWeight = FontWeight.Bold,
                    text = "Name",
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = textFieldValue.value,
                    onValueChange = {
                        textFieldValue.value = it
                    },
                    modifier = modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter your Name here.",
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    modifier = modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    fontWeight = FontWeight.Bold,
                    text = "Email",
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                    },
                    modifier = modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter your Email here.",
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }


        }
    }
}

@Preview
@Composable
private fun PreviewPersonalInfoPage() {
    PersonalInfoPage(
        name = "test",
        email = "a@gmail.com",
        snackbarHostState = SnackbarHostState()
    ) { }
}