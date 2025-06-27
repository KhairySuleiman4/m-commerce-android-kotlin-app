package com.example.mcommerce.presentation.search

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.usecases.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getAllProductsUseCase: GetAllProductsUseCase = mockk()
    private val getBrandsUseCase: GetBrandsUseCase = mockk()
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase = mockk()
    private val getCurrencyUseCase: GetCurrentCurrencyUseCase = mockk()
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase = mockk()
    private val isGuestModeUseCase: IsGuestModeUseCase = mockk()

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clearAllMocks()

        viewModel = SearchViewModel(
            productsUseCase = getAllProductsUseCase,
            brandsUseCase = getBrandsUseCase,
            getCurrencyUseCase = getCurrencyUseCase,
            getCurrentExchangeRateUseCase = getCurrentExchangeRateUseCase,
            getFavoriteProductsUseCase = getFavoriteProductsUseCase,
            insertProductToFavoritesUseCase = mockk(relaxed = true),
            deleteFavoriteProductUseCase = mockk(relaxed = true),
            isGuestModeUseCase = isGuestModeUseCase
        )
    }

    @Test
    fun getAllProductsAndBrands_success_updatesState() = runTest {
        // Given
        val products = listOf(
            ProductSearchEntity("1", "Adidas Shoe", "SHOES", "Adidas", 100.0, "", false),
            ProductSearchEntity("2", "Nike Shoe", "SHOES", "Nike", 150.0, "", false)
        )
        val brands = listOf(
            CollectionsEntity("1", "Adidas", "url1"),
            CollectionsEntity("2", "Nike", "url2")
        )
        coEvery { getAllProductsUseCase() } returns flowOf(ApiResult.Success(products))
        coEvery { getBrandsUseCase() } returns flowOf(ApiResult.Success(brands))
        coEvery { getFavoriteProductsUseCase() } returns flowOf(ApiResult.Success(emptyList()))
        every { isGuestModeUseCase() } returns false
        // When
        viewModel.getAllProductsAndBrands()
        advanceUntilIdle()
        // Then
        val state = viewModel.state.value
        assertEquals(2, state.allProducts.size)
        assertEquals(listOf("Adidas", "Nike"), state.brands)
    }

    @Test
    fun getCurrency_returnsCorrectCurrencyAndRate_updatesEventWithCurrencyData() = runTest {
        // Given
        val currency = "USD"
        val exchangeRate = 1.5
        coEvery { getCurrencyUseCase() } returns currency
        coEvery { getCurrentExchangeRateUseCase() } returns exchangeRate
        // When
        viewModel.getCurrency()
        advanceUntilIdle()
        // Then
        val event = viewModel.events.value
        assertTrue(event is SearchContract.Events.ShowCurrency)
        val showCurrencyEvent = event as SearchContract.Events.ShowCurrency
        assertEquals(currency, showCurrencyEvent.currency)
        assertEquals(exchangeRate, showCurrencyEvent.rate, 0.001)
    }

    @Test
    fun resetEvent_triggersIdleEvent() = runTest {
        // Given
        val currency = "USD"
        val exchangeRate = 1.5
        coEvery { getCurrencyUseCase() } returns currency
        coEvery { getCurrentExchangeRateUseCase() } returns exchangeRate
        viewModel.getCurrency()
        advanceUntilIdle()
        assertTrue(viewModel.events.value is SearchContract.Events.ShowCurrency)
        // When
        viewModel.resetEvent()
        // Then
        val event = viewModel.events.value
        assertTrue(event is SearchContract.Events.Idle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}