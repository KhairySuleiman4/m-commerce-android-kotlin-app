package com.example.mcommerce.presentation.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mcommerce.R
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.presentation.errors.SearchEmptyScreen
import com.example.mcommerce.presentation.favorites.FavoriteDeleteBottomSheet
import com.example.mcommerce.presentation.navigation.Screens
import com.example.mcommerce.presentation.theme.PoppinsFontFamily
import com.example.mcommerce.presentation.theme.Primary
import java.util.Locale

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    navigateTo: (Screens) -> Unit
) {
    val currency = remember { mutableStateOf("EGP") }
    val rate = remember { mutableDoubleStateOf(1.0) }
    val isGuest = remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllProductsAndBrands()
        viewModel.getCurrency()
        isGuest.value = viewModel.isGuest()
    }

    val event = viewModel.events.value

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(event) {
        when (event) {
            SearchContract.Events.Idle -> {

            }

            is SearchContract.Events.ShowCurrency -> {
                currency.value = event.currency
                rate.doubleValue = event.rate
            }

            is SearchContract.Events.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.msg,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetEvent()
            }
        }
    }

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

        PriceFilter(
            currency = currency.value,
            rate = rate.doubleValue
        ) { min, max ->
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

        ProductsList(
            filteredProducts = state.filteredProducts,
            currency = currency.value,
            rate = rate.doubleValue,
            onProductClick = {
                navigateTo(Screens.ProductDetails(it))
            },
            onFavoriteClick = {
                viewModel.invokeActions(SearchContract.Action.ClickOnFavoriteIcon(it))
            },
            isGuest = isGuest.value,
            snackbarHostState = snackbarHostState
        )

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
                label = {
                    Text(
                        fontFamily = PoppinsFontFamily,
                        text = type
                    )
                },
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
    currency: String,
    rate: Double,
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
            valueRange = 0f..500f,
            onValueChange = {
                sliderPosition.value = it
            },
            onValueChangeFinished = {
                onRangeSelected(sliderPosition.value.start, sliderPosition.value.endInclusive)
            },

            )
        Text(
            fontFamily = PoppinsFontFamily,
            text = "Min: ${(sliderPosition.value.start * rate).toInt()} $currency - Max: ${(sliderPosition.value.endInclusive * rate).toInt()} $currency",
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
            Text(
                fontFamily = PoppinsFontFamily,
                text = selectedBrand ?: "Select Brand"
            )
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
                    Text(
                        fontFamily = PoppinsFontFamily,
                        text = "All Brands"
                    )
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
                        Text(
                            it,
                            fontFamily = PoppinsFontFamily
                        )
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
                fontFamily = PoppinsFontFamily,
                color = Color.Gray
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsList(
    filteredProducts: List<ProductSearchEntity>,
    currency: String,
    rate: Double,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (ProductSearchEntity) -> Unit,
    isGuest: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedProduct = remember { mutableStateOf<ProductSearchEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Box {
        if (filteredProducts.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredProducts.size) { index ->

                    ProductCard(
                        product = filteredProducts[index],
                        currency = currency,
                        rate = rate,
                        onProductClick = onProductClick,
                        onFavoriteClick = {
                            if (!it.isFavorite) {
                                selectedProduct.value = it
                                showBottomSheet.value = true
                            } else {
                                onFavoriteClick(it)
                            }
                        },
                        isGuest = isGuest
                    )
                }
            }
        } else {
            SearchEmptyScreen()
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        if (showBottomSheet.value && selectedProduct.value != null) {
            FavoriteDeleteBottomSheet(
                productId = selectedProduct.value!!.id,
                onConfirmDelete = {
                    selectedProduct.value?.let { product ->
                        onFavoriteClick(product)
                    }
                    selectedProduct.value = null
                    showBottomSheet.value = false
                },
                onCancel = {
                    selectedProduct.value = null
                    showBottomSheet.value = false
                },
                sheetState = sheetState
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductCard(
    product: ProductSearchEntity,
    currency: String,
    rate: Double,
    isGuest: Boolean,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (ProductSearchEntity) -> Unit,
    modifier: Modifier = Modifier
) {

    val isFavorite = remember { mutableStateOf(product.isFavorite) }

    LaunchedEffect(product.isFavorite) {
        isFavorite.value = product.isFavorite
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(4.dp)
            .height(250.dp)
            .width(150.dp)
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
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = {
                        if (!isGuest) {
                            isFavorite.value = !isFavorite.value
                            val newProduct = product.copy(isFavorite = !product.isFavorite)
                            onFavoriteClick(newProduct)
                        } else {
                            onFavoriteClick(product.copy(isFavorite = true))
                        }
                    },
                    modifier = modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        modifier = modifier.size(30.dp),
                        imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                        tint = if (isFavorite.value) Color.Red else Color.DarkGray,
                        contentDescription = stringResource(R.string.favorite_icon)
                    )
                }
            }
            Text(
                fontFamily = PoppinsFontFamily,
                text = product.title,
                modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 2
            )
            Text(
                fontFamily = PoppinsFontFamily,
                text = "${product.brand} | ${product.productType}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(end = 8.dp, start = 8.dp, bottom = 4.dp),
            ) {
                Text(
                    fontFamily = PoppinsFontFamily,
                    text = "$currency ${String.format(Locale.US, "%.2f", (product.price * rate))}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}