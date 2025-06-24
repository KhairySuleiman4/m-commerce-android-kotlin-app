package com.example.mcommerce.presentation.categories

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.data.utils.imagesMapper
import com.example.mcommerce.domain.entities.CategoriesEntity
import com.example.mcommerce.presentation.errors.FailureScreen
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.PoppinsFontFamily

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
    navigateTo: (Screens) -> Unit,
) {
    val event = viewModel.events.value
    val state = viewModel.states.value

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    LaunchedEffect(event) {
        when (event) {
            CategoriesContract.Events.Idle -> {}
            is CategoriesContract.Events.NavigateToCategoryProducts -> {
                navigateTo(Screens.Products(event.collectionId, event.collectionName))
                viewModel.resetEvent()
            }
        }
    }
    Categories(
        state = state,
        onCategoryClick = { id, name ->
            viewModel.invokeActions(CategoriesContract.Action.ClickOnCategory(id, name))
        }
    )


}

@Composable
fun Categories(
    modifier: Modifier = Modifier,
    state: CategoriesContract.States,
    onCategoryClick: (String, String) -> Unit,
) {
    when (state) {
        is CategoriesContract.States.Failure -> {
            FailureScreen(state.errorMessage)
        }

        CategoriesContract.States.Idle -> {}
        CategoriesContract.States.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is CategoriesContract.States.Success -> {
            CategoriesList(
                categoriesList = state.categoriesList,
                onCategoryClick = onCategoryClick
            )
        }
    }
}

@Composable
fun CategoriesList(
    modifier: Modifier = Modifier,
    categoriesList: List<CategoriesEntity>,
    onCategoryClick: (String, String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(categoriesList) { category ->
            CategoriesCard(
                category = category,
                onCategoryClick = onCategoryClick
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CategoriesCard(
    modifier: Modifier = Modifier,
    category: CategoriesEntity,
    onCategoryClick: (String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color(0xFFd1b9b1)),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(180.dp)
            .clickable { onCategoryClick(category.id, category.title) },
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                fontFamily = PoppinsFontFamily,
                text = category.description,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.White,
                maxLines = 2,
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .width(120.dp)
            )
            GlideImage(
                model = imagesMapper(category.imageUrl),
                contentDescription = category.title,
                modifier = modifier
                    .height(172.dp)
                    .width(180.dp)
                    .align(Alignment.Bottom),
                contentScale = ContentScale.Fit
            )
        }
    }
}