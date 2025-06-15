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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navigationTo: (Screens)-> Unit
) {

    val event = viewModel.events.value

    LaunchedEffect(event) {
        when(event){
            is ProfileContract.Event.Idle -> {}
            is ProfileContract.Event.Logout -> {
                viewModel.resetEvent()
            }
        }
    }

    val tabItems = listOf(
        ProfileItem(R.drawable.profile_icon,"Personal Information", Screens.Profile),
        ProfileItem(R.drawable.favourites_icon,"Favourites", Screens.Profile),
        ProfileItem(R.drawable.shopping_icon,"My Orders", Screens.Profile),
        ProfileItem(R.drawable.credit_icon,"Payment method", Screens.Profile),
        ProfileItem(R.drawable.settings_icon,"Settings", Screens.Settings),
        ProfileItem(R.drawable.info_icon,"About us", Screens.Maps),
        ProfileItem(R.drawable.logout_icon,"Logout", Screens.Profile),
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            UserInfo(email = "example@gmail.com", name = "Full Name")
        }

        items(tabItems.size){
            ProfileTab(
                modifier = Modifier.clickable {
                    if(tabItems[it].text == "Logout"){
                        viewModel.invokeActions(ProfileContract.Action.ClickOnLogout)
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
        modifier= modifier,
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
        )

        Text(
            text,
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
            .background(color = Background)
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
        Text(
            ">",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
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
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .width(75.dp)
                .height(75.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(email)
        }
    }
}

@Preview
@Composable
private fun PreviewProfileTab() {
    ProfileTab(image = R.drawable.profile_icon, text = "Personal Information")
}