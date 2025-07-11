package com.example.mcommerce.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcommerce.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.profile.models.ProfileItem
import com.example.mcommerce.presentation.theme.Background
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    navigationTo: (Screens) -> Unit
) {
    val isGuest = remember { mutableStateOf(true) }
    val email = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val event = viewModel.events.value

    LaunchedEffect(Unit) {
        viewModel.setup()
    }

    LaunchedEffect(event) {
        when (event) {
            is ProfileContract.Event.Idle -> {}
            is ProfileContract.Event.Logout -> {
                viewModel.resetEvent()
            }

            is ProfileContract.Event.UpdateData -> {
                isGuest.value = event.isGuest
                email.value = event.email
                name.value = event.name
            }
        }
    }

    val loggedTabItems = listOf(
        ProfileItem(R.drawable.profile_icon,"Personal Information", Screens.PersonalInfo),
        ProfileItem(R.drawable.adress_icon,"My Addresses", Screens.Addresses),
        ProfileItem(R.drawable.shopping_icon,"My Orders", Screens.OrdersScreen),
        ProfileItem(R.drawable.settings_icon,"Settings", Screens.Settings),
        ProfileItem(R.drawable.info_icon,"About us", Screens.AboutUs),
        ProfileItem(R.drawable.logout_icon,"Logout", Screens.Login),
    )

    val guestTabItems = listOf(
        ProfileItem(R.drawable.settings_icon,"Settings", Screens.Settings),
        ProfileItem(R.drawable.info_icon,"About us", Screens.AboutUs),
        ProfileItem(R.drawable.profile_icon,"Login", Screens.Login),
    )

    ProfilePage(
        modifier,
        !isGuest.value,
        email.value,
        name.value,
        if (isGuest.value) guestTabItems else loggedTabItems,
        {
            viewModel.invokeActions(ProfileContract.Action.ClickOnLogout)
        },
        navigationTo
    )

}

@Composable
private fun ProfilePage(
    modifier: Modifier,
    isLoggedIn: Boolean,
    email: String,
    name: String,
    tabItems: List<ProfileItem>,
    logoutAction: () -> Unit,
    navigationTo: (Screens) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (isLoggedIn)
                UserInfo(email = email, name = name)
            else
                UserInfo(email = "", name = "Guest")
        }

        items(tabItems.size) {
            ProfileTab(
                modifier = Modifier.clickable {
                    if (tabItems[it].route == Screens.Login) {
                        logoutAction()
                    }
                    navigationTo(tabItems[it].route)
                },
                image = tabItems[it].image,
                text = tabItems[it].text
            )
        }
    }
}

@Composable
fun ProfileTabInfo(
    modifier: Modifier = Modifier,
    image: Int,
    text: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = text,
            colorFilter = ColorFilter.tint(color = Primary),
            modifier = Modifier
                .width(25.dp)
                .height(25.dp)
                .padding(end = 4.dp)
        )

        Text(
            fontFamily = PoppinsFontFamily,
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun ProfileTab(
    modifier: Modifier = Modifier,
    image: Int,
    text: String
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileTabInfo(
            image = image,
            text = text
        )
        Icon(
            painter = painterResource(R.drawable.forward_arrow),
            tint = Primary,
            contentDescription =""
        )
    }
}

@Composable
fun UserInfo(
    modifier: Modifier = Modifier,
    email: String,
    name: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.profile_placeholder),
            contentDescription = "user image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(75.dp)
                .height(75.dp)
                .clip(RoundedCornerShape(50))
        )
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                fontFamily = PoppinsFontFamily,
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                fontFamily = PoppinsFontFamily, text = email
            )
        }
    }
}

@Preview
@Composable
private fun PreviewProfileTab() {
    ProfileTab(image = R.drawable.profile_icon, text = "Personal Information")
}