package com.example.mcommerce.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.presentation.navigation.Screens

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    navigateTo: (Screens) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {

        item {
            TypesFilter(
                state.filter.type
            ){ type ->
                viewModel.invokeActions(
                    SearchContract.Action.OnTypeSelected(
                        if (state.filter.type == type) null else type
                    )
                )
            }
        }

        item {
            PriceFilter { min, max ->
                viewModel.invokeActions(
                    SearchContract.Action.OnPriceRangeChanged(min.toDouble(), max.toDouble())
                )
            }
        }

        item {
            BrandDropdownMenu(
                selectedBrand = state.filter.brand,
                brands = state.brands,
                onBrandSelected = { brand ->
                    viewModel.invokeActions(SearchContract.Action.OnBrandSelected(brand))
                }
            )
        }

        item {
            SearchBar(
                searchQuery = state.filter.searchQuery
            ){ query ->
                viewModel.invokeActions(SearchContract.Action.OnSearchQueryChanged(query))
            }
        }

        items(state.filteredProducts.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                val product = state.filteredProducts[index]
                Text("${index + 1}: ${product.title}\t${product.price}\t${product.productType}\t${product.brand}\n")
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
    ){
        listOf("Accessories", "Shoes", "T-Shirts").forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = {
                    onTypeSelected(type)
                },
                label = { Text(type) }
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
            .padding(16.dp)
    ) {
        RangeSlider(
            value = sliderPosition.value,
            steps = 50,
            valueRange = 0f..500f,
            onValueChange = {
                sliderPosition.value = it
            },
            onValueChangeFinished = {
                onRangeSelected(sliderPosition.value.start, sliderPosition.value.endInclusive)
            }
        )
        Text(
            "Min: ${sliderPosition.value.start.toInt()} - Max: ${sliderPosition.value.endInclusive.toInt()}"
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

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(text = selectedBrand ?: "Select Brand")
        }

        DropdownMenu(
            modifier = modifier
                .padding(horizontal = 16.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
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
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = searchQuery,
        onValueChange = {
            onSearchQueryChanged(it)
        },
        label = { Text("Search Products") }
    )
}