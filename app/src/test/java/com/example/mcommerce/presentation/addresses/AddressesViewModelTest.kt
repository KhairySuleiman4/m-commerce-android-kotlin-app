package com.example.mcommerce.presentation.addresses

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.usecases.ChangeDefaultAddressUseCase
import com.example.mcommerce.domain.usecases.GetCustomerAddressesUseCase
import com.example.mcommerce.domain.usecases.GetUserAccessTokenUseCase
import com.example.mcommerce.domain.usecases.RemoveAddressUseCase
import com.google.gson.JsonObject
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
class AddressesViewModelTest {

    private lateinit var viewModel: AddressesViewModel

    private val changeDefaultAddressUseCase: ChangeDefaultAddressUseCase = mockk(relaxed = true)
    private val getCustomerAddressesUseCase: GetCustomerAddressesUseCase = mockk()
    private val removeAddressUseCase: RemoveAddressUseCase = mockk(relaxed = true)
    private val getUserAccessTokenUseCase: GetUserAccessTokenUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val fakeToken = "fake_token"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val tokenJson = JsonObject().apply { addProperty("token", fakeToken) }
        coEvery { getUserAccessTokenUseCase() } returns tokenJson.toString()
        viewModel = AddressesViewModel(
            changeDefaultAddressUseCase,
            getCustomerAddressesUseCase,
            removeAddressUseCase,
            getUserAccessTokenUseCase
        )
    }



    @Test
    fun getAddresses_emitsSuccessState() = runTest {
        //given
        val addresses = listOf(
            AddressEntity(
                id = "1",
                name = "Street",
                subName = "Area",
                country = "Country",
                city = "City",
                zip = "ZIP",
                latitude = 0.0,
                longitude = 0.0
            )
        )
        coEvery { getCustomerAddressesUseCase(fakeToken) } returns flowOf(ApiResult.Success(addresses))

        //when
        viewModel.getAddresses()
        advanceUntilIdle()

        //then
        assertEquals(
            AddressesContract.States.Success(addresses),
            viewModel.states.value
        )
    }

    @Test
    fun invokeAction_ClickOnSetDefault_emitsSavedEventOnSuccess() = runTest {
        //given
        val addressId = "123"
        coEvery { changeDefaultAddressUseCase(fakeToken, addressId) } returns flowOf(ApiResult.Success(true))
        coEvery { getCustomerAddressesUseCase(fakeToken) } returns flowOf(ApiResult.Success(emptyList()))

        //when
        viewModel.invokeActions(AddressesContract.Action.ClickOnSetDefault(addressId))
        advanceUntilIdle()

        //then
        assertEquals(
            AddressesContract.Events.ShowError("Saved Successfully!"),
            viewModel.events.value
        )
    }

    @Test
    fun invokeAction_ClickOnDelete_emitsDeleteEventOnSuccess() = runTest {
        //given
        val addressId = "123"
        coEvery { removeAddressUseCase(fakeToken, addressId) } returns flowOf(ApiResult.Success(true))
        coEvery { getCustomerAddressesUseCase(fakeToken) } returns flowOf(ApiResult.Success(emptyList()))

        //when
        viewModel.invokeActions(AddressesContract.Action.ClickOnDelete(addressId))
        advanceUntilIdle()

        //then
        assertEquals(
            AddressesContract.Events.ShowError("Deleted Successfully!"),
            viewModel.events.value
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}