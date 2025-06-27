package com.example.mcommerce.presentation.favorites

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.domain.usecases.GetFavoriteProductsUseCase
import io.mockk.coEvery
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
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase = mockk()
    private val getCurrencyUseCase: GetCurrentCurrencyUseCase = mockk()
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase = mockk()

    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FavoritesViewModel(
            getFavoriteProductsUseCase = getFavoriteProductsUseCase,
            deleteFavoriteProductUseCase = mockk(relaxed = true),
            getCurrencyUseCase = getCurrencyUseCase,
            getCurrentExchangeRateUseCase = getCurrentExchangeRateUseCase
        )
    }

    @Test
    fun getCurrency_dummyCurrency_triggersShowCurrencyEvent() = runTest {
        // Given
        val currency = "USD"
        val rate = 1.2
        coEvery { getCurrencyUseCase() } returns currency
        coEvery { getCurrentExchangeRateUseCase() } returns rate
        // When
        viewModel.getCurrency()
        advanceUntilIdle()
        // Then
        val event = viewModel.events.value
        assertTrue(event is FavoritesContract.Events.ShowCurrency)
        val showCurrencyEvent = event as FavoritesContract.Events.ShowCurrency
        assertEquals(currency, showCurrencyEvent.currency)
        assertEquals(rate, showCurrencyEvent.rate)
    }

    @Test
    fun getFavorites_updatesStateToSuccess() = runTest {
        // Given
        val products = listOf(
            ProductSearchEntity(
                id = "1",
                title = "Test Product",
                price = 100.0,
                productType = "SHOES",
                brand = "Nike",
                imageUrl = "image.jpg",
                isFavorite = true
            )
        )
        coEvery { getFavoriteProductsUseCase() } returns flowOf(ApiResult.Success(products))
        // When
        viewModel.getFavoriteProducts()
        advanceUntilIdle()
        // Then
        val state = viewModel.states.value
        assertTrue("Expected Success state, got $state", state is FavoritesContract.States.Success)
        val successState = state as FavoritesContract.States.Success
        assertEquals(1, successState.products.size)
        assertEquals("1", successState.products.first().id)
    }

    @Test
    fun resetEvent_triggersIdleEvent() = runTest {
        // Given
        val currency = "EGP"
        coEvery { getCurrencyUseCase() } returns currency
        coEvery { getCurrentExchangeRateUseCase() } returns 30.0
        viewModel.getCurrency()
        advanceUntilIdle()
        assertTrue(viewModel.events.value is FavoritesContract.Events.ShowCurrency)
        // When
        viewModel.resetEvent()
        // Then
        assertTrue(viewModel.events.value is FavoritesContract.Events.Idle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}