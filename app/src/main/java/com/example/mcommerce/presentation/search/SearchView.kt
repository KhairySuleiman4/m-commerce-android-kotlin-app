package com.example.mcommerce.presentation.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.Primary

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    navigateTo: (Screens) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        TypesFilter(
            state.filter.type
        ) { type ->
            viewModel.invokeActions(
                SearchContract.Action.OnTypeSelected(
                    if (state.filter.type == type) null else type
                )
            )
        }

        PriceFilter { min, max ->
            viewModel.invokeActions(
                SearchContract.Action.OnPriceRangeChanged(min.toDouble(), max.toDouble())
            )
        }

        BrandDropdownMenu(
            selectedBrand = state.filter.brand,
            brands = state.brands,
            onBrandSelected = { brand ->
                viewModel.invokeActions(SearchContract.Action.OnBrandSelected(brand))
            }
        )

        SearchBar(
            searchQuery = state.filter.searchQuery
        ) { query ->
            viewModel.invokeActions(SearchContract.Action.OnSearchQueryChanged(query))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize()
        ) {
            items(state.filteredProducts.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    ProductCard(
                        product = state.filteredProducts[index],
                        onProductClick = {
                            navigateTo(Screens.ProductDetails(it))
                        },
                        onFavoriteClick = {
                            //viewModel.invokeActions(SearchContract.Action.OnAddToFavorite(it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TypesFilter(
    selectedType: String?,
    modifier: Modifier = Modifier,
    onTypeSelected: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf("Accessories", "Shoes", "T-Shirts").forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = {
                    onTypeSelected(type)
                },
                label = { Text(type) },
                colors = SelectableChipColors(
                    containerColor = Color.White,
                    labelColor = Primary,
                    leadingIconColor = Primary,
                    trailingIconColor = Primary,
                    disabledContainerColor = Color.Gray,
                    disabledLabelColor = Color.Gray,
                    disabledLeadingIconColor = Color.White,
                    disabledTrailingIconColor = Color.White,
                    selectedContainerColor = Primary,
                    disabledSelectedContainerColor = Color.White,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White,
                    selectedTrailingIconColor = Color.White
                ),
                border = BorderStroke(0.5.dp, Primary)
            )
        }
    }
}

@Composable
fun PriceFilter(
    modifier: Modifier = Modifier,
    onRangeSelected: (Float, Float) -> Unit
) {
    val sliderPosition = remember {
        mutableStateOf(0f..500f)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RangeSlider(
            colors = SliderColors(
                thumbColor = Primary,
                activeTrackColor = Primary,
                activeTickColor = Color.White,
                inactiveTrackColor = Color.White,
                inactiveTickColor = Primary,
                disabledThumbColor = Color.Gray,
                disabledActiveTrackColor = Color.Gray,
                disabledActiveTickColor = Color.Gray,
                disabledInactiveTrackColor = Color.Gray,
                disabledInactiveTickColor = Color.Gray
            ),
            value = sliderPosition.value,
            //steps = 19,
            valueRange = 0f..500f,
            onValueChange = {
                sliderPosition.value = it
            },
            onValueChangeFinished = {
                onRangeSelected(sliderPosition.value.start, sliderPosition.value.endInclusive)
            },

            )
        Text(
            "Min: ${sliderPosition.value.start.toInt()} - Max: ${sliderPosition.value.endInclusive.toInt()}",
            fontSize = 20.sp
        )
    }
}

@Composable
fun BrandDropdownMenu(
    selectedBrand: String?,
    brands: List<String>,
    onBrandSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        TextButton(
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Primary,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Gray
            ),
            border = BorderStroke(
                0.5.dp,
                Primary
            ),
            onClick = {
                expanded = true
            }
        ) {
            Text(text = selectedBrand ?: "Select Brand")
        }

        DropdownMenu(
            modifier = modifier
                .padding(horizontal = 16.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                onClick = {
                    onBrandSelected(null)
                    expanded = false
                },
                text = {
                    Text("All Brands")
                }
            )

            brands.forEach {
                DropdownMenuItem(
                    colors = MenuItemColors(
                        textColor = Primary,
                        leadingIconColor = Primary,
                        trailingIconColor = Primary,
                        disabledTextColor = Color.Gray,
                        disabledLeadingIconColor = Color.Gray,
                        disabledTrailingIconColor = Color.Gray
                    ),
                    onClick = {
                        onBrandSelected(it)
                        expanded = false
                    },
                    text = {
                        Text(it)
                    }
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    modifier: Modifier = Modifier,
    onSearchQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        value = searchQuery,
        onValueChange = {
            onSearchQueryChanged(it)
        },
        placeholder = {
            Text(
                "Search Products",
                color = Color.Gray
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductCard(
    product: ProductSearchEntity,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (ProductSearchEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAddedToFavorite = remember {
        mutableStateOf(false)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(4.dp)
            .height(350.dp)
            .clickable { onProductClick(product.id) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box {
                GlideImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = {
                        isAddedToFavorite.value = !isAddedToFavorite.value
                        onFavoriteClick(product)
                    },
                    modifier = modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isAddedToFavorite.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = modifier.size(30.dp),
                        tint = Color(0xFFD32F2F)
                    )
                }
            }
            Spacer(modifier.height(8.dp))
            Text(
                text = product.title,
                modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 2
            )

            Text(
                text = "${product.brand} | ${product.productType}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "EGP ${product.price}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier.height(8.dp))
        }
    }
}