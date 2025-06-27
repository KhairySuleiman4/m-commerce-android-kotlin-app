package com.example.mcommerce.presentation.map.viewmodel

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.usecases.AddAddressUseCase
import com.example.mcommerce.domain.usecases.GetAddressesUseCase
import com.example.mcommerce.domain.usecases.GetUserAccessTokenUseCase
import com.example.mcommerce.domain.usecases.GetUserNameUseCase
import com.example.mcommerce.presentation.map.MapContract
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
class MapViewModelTest {

    private lateinit var viewModel: MapViewModel

    private val useCase: GetAddressesUseCase = mockk()
    private val addAddressUseCase: AddAddressUseCase = mockk()
    private val getUserAccessTokenUseCase: GetUserAccessTokenUseCase = mockk()
    private val getUserNameUseCase: GetUserNameUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MapViewModel(
            useCase,
            addAddressUseCase,
            getUserAccessTokenUseCase,
            getUserNameUseCase
        )
    }

    @Test
    fun getSelectedLocation_emitsChangedAddressEvent() = runTest {
        //given
        val latitude = 30.0
        val longitude = 31.0
        val address = AddressEntity("id", "street", "area", "country", "city", "zip", latitude, longitude)
        coEvery { useCase(latitude, longitude) } returns flowOf(ApiResult.Success(address))

        //when
        viewModel.getSelectedLocation(latitude, longitude)
        advanceUntilIdle()

        //then
        assertEquals(MapContract.Events.ChangedAddress(address), viewModel.events.value)
    }

    @Test
    fun getSearchResult_emitsSuccessState() = runTest {
        //given
        val query = "Cairo"
        val addresses = listOf(
            Triple("id", "name", "country")
        )
        coEvery { useCase(query) } returns flowOf(ApiResult.Success(addresses))

        //when
        viewModel.invokeActions(MapContract.Action.SearchPlace(query))
        advanceUntilIdle()

        //then
        assertEquals(MapContract.States.Success(addresses), viewModel.states.value)
    }

    @Test
    fun addAddress_emitsSavedAddressEvent() = runTest {
        //given
        val address = AddressEntity("id", "street", "area", "country", "city", "zip", 0.0, 0.0)
        val tokenJson = JsonObject().apply { addProperty("token", "access_token") }
        coEvery { getUserAccessTokenUseCase() } returns tokenJson.toString()
        coEvery { getUserNameUseCase() } returns "Username"
        coEvery { addAddressUseCase("access_token", address, "Username") } returns flowOf(ApiResult.Success(true))

        //when
        viewModel.invokeActions(MapContract.Action.ClickOnSave(address))
        advanceUntilIdle()

        //then
        assertEquals(MapContract.Events.SavedAddress, viewModel.events.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
