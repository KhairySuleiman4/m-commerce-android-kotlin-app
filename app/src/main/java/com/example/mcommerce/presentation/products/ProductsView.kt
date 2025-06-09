package com.example.mcommerce.presentation.products

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ProductsScreen(modifier: Modifier = Modifier, brandId: String) {
   Box(
       modifier = modifier.fillMaxSize(),
       contentAlignment = Alignment.Center
   ) {
       Text(
           text = brandId,
       )
       Log.i("TAG", brandId)
   }

}