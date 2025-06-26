package com.example.mcommerce.presentation.orders

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.LineItemEntity
import com.example.mcommerce.domain.entities.OrderEntity
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.domain.usecases.GetOrdersUseCase
import com.example.mcommerce.domain.usecases.GetUserAccessTokenUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val ordersUseCase: GetOrdersUseCase = mockk()
    private var getUserAccessTokenUseCase: GetUserAccessTokenUseCase = mockk()
    private var getCurrentCurrencyUseCase: GetCurrentCurrencyUseCase = mockk()
    private var getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase = mockk()

    private lateinit var viewModel: OrdersViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = OrdersViewModel(
            ordersUseCase = ordersUseCase,
            getUserAccessTokenUseCase = getUserAccessTokenUseCase,
            getCurrentCurrencyUseCase = getCurrentCurrencyUseCase,
            getCurrentExchangeRateUseCase = getCurrentExchangeRateUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getOrders_listOfOrders_updateStateWithSuccess() =
        runTest {
            // given
            val ordersList = listOf(
                OrderEntity(
                    name = "1024",
                    processedAt = "2025-06-25T17:32:25Z",
                    subtotalPrice = "269.95",
                    totalPrice = "307.74",
                    shippingAddress = "Ahmed Abou Zaid Road",
                    city = "Ismailia Governorate",
                    customerName = "khairy",
                    phone = "+201257798654",
                    lineItems = listOf(
                        LineItemEntity(
                            quantity = "1",
                            variantTitle = "4 / black",
                            productId = "1",
                            productTitle = "t-shirt",
                            price = "140.0",
                            imageUrl = "t-shirt image"
                        )
                    )
                ),
                OrderEntity(
                    name = "1024",
                    processedAt = "2025-06-25T17:32:25Z",
                    subtotalPrice = "269.95",
                    totalPrice = "307.74",
                    shippingAddress = "Ahmed Abou Zaid Road",
                    city = "Ismailia Governorate",
                    customerName = "khairy",
                    phone = "+201257798654",
                    lineItems = listOf(
                        LineItemEntity(
                            quantity = "1",
                            variantTitle = "4 / black",
                            productId = "1",
                            productTitle = "t-shirt",
                            price = "140.0",
                            imageUrl = "t-shirt image"
                        )
                    )
                )
            )
            val flow = flowOf(ApiResult.Success(ordersList))

            coEvery { getUserAccessTokenUseCase() } returns """{"token":"fake_token"}"""
            coEvery { ordersUseCase("fake_token") } returns flow

            // when
            viewModel.getOrders()
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            val state = viewModel.states.value
            assertTrue(state is OrdersContract.States.Success)
            assertEquals(ordersList, (state as OrdersContract.States.Success).ordersList)
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