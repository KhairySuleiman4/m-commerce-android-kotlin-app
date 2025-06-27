package com.example.mcommerce.presentation.cart.viewmodel

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CartEntity
import com.example.mcommerce.domain.entities.LineEntity
import com.example.mcommerce.domain.usecases.AddDiscountToCartUseCase
import com.example.mcommerce.domain.usecases.ChangeCartItemInCartUseCase
import com.example.mcommerce.domain.usecases.CheckForDefaultAddressUseCase
import com.example.mcommerce.domain.usecases.ClearLocalCartUseCase
import com.example.mcommerce.domain.usecases.ClearOnlineCartUseCase
import com.example.mcommerce.domain.usecases.GetCartUseCase
import com.example.mcommerce.domain.usecases.GetCurrentCurrencyUseCase
import com.example.mcommerce.domain.usecases.GetCurrentExchangeRateUseCase
import com.example.mcommerce.domain.usecases.GetUserAccessTokenUseCase
import com.example.mcommerce.domain.usecases.RemoveItemFromCartUseCase
import com.example.mcommerce.presentation.cart.CartContract
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private lateinit var viewModel: CartViewModel

    private val getCartUseCase: GetCartUseCase = mockk()
    private val changeQuantityUseCase: ChangeCartItemInCartUseCase = mockk()
    private val removeItemFromCartUseCase: RemoveItemFromCartUseCase = mockk()
    private val addDiscountToCartUseCase: AddDiscountToCartUseCase = mockk()
    private val getCurrentCurrencyUseCase: GetCurrentCurrencyUseCase = mockk()
    private val getCurrentExchangeRateUseCase: GetCurrentExchangeRateUseCase = mockk()
    private val checkForDefaultAddressUseCase: CheckForDefaultAddressUseCase = mockk()
    private val getUserAccessTokenUseCase: GetUserAccessTokenUseCase = mockk()
    private val clearOnlineCartUseCase: ClearOnlineCartUseCase = mockk()
    private val clearLocalCartUseCase: ClearLocalCartUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private val fakeCart = CartEntity(
        id = "cartId",
        checkout = "checkoutUrl",
        subtotalAmount = 100.0,
        totalAmount = 120.0,
        discountAmount = 0.0,
        items = listOf(
            LineEntity(
                id = "item1",
                quantity = 1,
                price = 100.0,
                image = "imageUrl",
                title = "Product",
                category = "Category",
                brand = "Brand",
                lineId = "lineId1"
            )
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CartViewModel(
            getCartUseCase,
            changeQuantityUseCase,
            addDiscountToCartUseCase,
            removeItemFromCartUseCase,
            getCurrentCurrencyUseCase,
            getCurrentExchangeRateUseCase,
            checkForDefaultAddressUseCase,
            getUserAccessTokenUseCase,
            clearOnlineCartUseCase,
            clearLocalCartUseCase
        )
    }

    @Test
    fun getCart_emitSuccessState() = runTest {
        //given
        coEvery { getCartUseCase() } returns flowOf(ApiResult.Success(fakeCart))

        //when
        viewModel.getCart()
        advanceUntilIdle()

        //then
        assertEquals(CartContract.States.Success(fakeCart), viewModel.states.value)
    }

    @Test
    fun changeQuantity_emitsSuccessState() = runTest {
        //given
        val newCart = fakeCart.copy(items = fakeCart.items)
        coEvery { changeQuantityUseCase("item1", 2) } returns flowOf(ApiResult.Success(newCart))

        //when
        viewModel.invokeActions(CartContract.Action.ClickOnPlusItem("item1", 1))
        advanceUntilIdle()

        //then
        assertEquals(CartContract.States.Success(newCart), viewModel.states.value)
    }

    @Test
    fun removeItem_emitsSuccessState() = runTest {
        //given
        val updatedCart = fakeCart.copy(items = emptyList())
        coEvery { removeItemFromCartUseCase("item1") } returns flowOf(ApiResult.Success(updatedCart))

        //when
        viewModel.invokeActions(CartContract.Action.ClickOnRemoveItem("item1"))
        advanceUntilIdle()

        //then
        assertEquals(CartContract.States.Success(updatedCart), viewModel.states.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
