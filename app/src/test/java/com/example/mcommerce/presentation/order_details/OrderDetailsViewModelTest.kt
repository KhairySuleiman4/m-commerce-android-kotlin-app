package com.example.mcommerce.presentation.order_details

import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.presentation.orders.OrdersContract
import com.example.mcommerce.presentation.orders.OrdersViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private var getCurrentCurrencyUseCase: GetCurrentCurrencyUseCase = mockk()
    private var getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase = mockk()

    private lateinit var viewModel: OrdersViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = OrdersViewModel(
            ordersUseCase = mockk(relaxed = true),
            getUserAccessTokenUseCase = mockk(relaxed = true),
            getCurrentCurrencyUseCase = getCurrentCurrencyUseCase,
            getCurrentExchangeRateUseCase = getCurrentExchangeRateUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getCurrency_emitShowCurrencyEvent() =
        runTest {
            // given
            coEvery { getCurrentCurrencyUseCase() } returns "EGP"
            coEvery { getCurrentExchangeRateUseCase() } returns 30.0

            // when
            viewModel.getCurrency()
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            val event = viewModel.events.value
            assertTrue(event is OrdersContract.Events.ShowCurrency)
            val showCurrencyEvent = event as OrdersContract.Events.ShowCurrency
            assertEquals("EGP", showCurrencyEvent.currency)
            assertEquals(30.0, showCurrencyEvent.rate, 0.0)
        }
}