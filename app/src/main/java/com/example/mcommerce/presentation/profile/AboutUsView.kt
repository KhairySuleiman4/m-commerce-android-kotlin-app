package com.example.mcommerce.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutUsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("About Pick’n Pay", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = modifier.height(16.dp))

        Text("Pick’n Pay is a mobile e-commerce application designed to offer users a smooth, minimalist shopping experience.")

        Spacer(modifier = modifier.height(24.dp))

        Text("Team Members", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = modifier.height(12.dp))
        Text("Hazem Mahmoud — Android Developer")
        Text("Khairy Hatem — Android Developer", modifier = modifier.padding(vertical = 4.dp))
        Text("Shereen Mohamed — Android Developer")

        Spacer(modifier = modifier.height(24.dp))

        Text("Supervised by Eng. Yasmeen Hosny", fontStyle = FontStyle.Italic)

        Spacer(modifier = modifier.height(24.dp))

        Text("Version 1.0.0", color = Color.Gray, fontSize = 14.sp)
    }
}