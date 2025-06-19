package com.example.mcommerce.presentation.favorites

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.presentation.navigation.Screens

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navigationTo: (Screens)-> Unit
) {

}