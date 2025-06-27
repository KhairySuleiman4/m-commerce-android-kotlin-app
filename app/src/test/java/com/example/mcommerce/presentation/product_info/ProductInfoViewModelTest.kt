package com.example.mcommerce.presentation.product_info

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductVariantEntity
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.domain.usecases.GetProductByIdUseCase
import com.example.mcommerce.domain.usecases.IsGuestModeUseCase
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
class ProductInfoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getProductByIdUseCase: GetProductByIdUseCase = mockk()
    private val getCurrencyUseCase: GetCurrentCurrencyUseCase = mockk()
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase = mockk()
    private val isGuestModeUseCase: IsGuestModeUseCase = mockk()

    private lateinit var viewModel: ProductInfoViewModel

    private val dummyVariant = ProductVariantEntity(
        id = "v123",
        imageUrl = "image_url",
        title = "Variant 1",
        price = "100.0",
        isSelected = false
    )

    private val dummyProduct = ProductInfoEntity(
        id = "p1",
        images = listOf("image1.jpg", "image2.jpg"),
        title = "Test Product",
        price = 100.0,
        priceUnit = "EGP",
        productType = "SHOES",
        vendor = "Test Vendor",
        description = "Test Description",
        variants = listOf(dummyVariant),
        isFavorite = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clearAllMocks()

        viewModel = ProductInfoViewModel(
            addItemToCartUseCase = mockk(relaxed = true),
            getCartUseCase = mockk(relaxed = true),
            getProductUseCase = getProductByIdUseCase,
            insertToFavoritesUseCase = mockk(relaxed = true),
            getFavoritesUseCase = mockk(relaxed = true),
            deleteFavoriteProductUseCase = mockk(relaxed = true),
            getCurrencyUseCase = getCurrencyUseCase,
            getCurrentExchangeRateUseCase = getCurrentExchangeRateUseCase,
            isGuestModeUseCase = isGuestModeUseCase
        )
    }

    @Test
    fun getProductById_dummyProduct_updatesStateToSuccess() = runTest {
        // Given
        coEvery { getProductByIdUseCase("p1") } returns flowOf(ApiResult.Success(dummyProduct))
        every { isGuestModeUseCase() } returns true
        // When
        viewModel.getProductById("p1")
        advanceUntilIdle()
        // Then
        val state = viewModel.states.value
        assertTrue(state is ProductInfoContract.States.Success)
        assertEquals("p1", (state as ProductInfoContract.States.Success).product.id)
        assertEquals("Test Product", state.product.title)
    }

    @Test
    fun getCurrency_updatesEventWithCurrencyAndRate() = runTest {
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
        assertTrue(event is ProductInfoContract.Events.ShowCurrency)
        val showCurrencyEvent = event as ProductInfoContract.Events.ShowCurrency
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

        assertTrue(viewModel.events.value is ProductInfoContract.Events.ShowCurrency)

        // When
        viewModel.resetEvent()

        // Then
        val event = viewModel.events.value
        assertTrue(event is ProductInfoContract.Events.Idle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}