package com.example.mcommerce.presentation.personalinfo

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.GetEmailUseCase
import com.example.mcommerce.domain.usecases.GetUserNameUseCase
import com.example.mcommerce.domain.usecases.UpdateUserNameUseCase
import io.mockk.coEvery
import io.mockk.every
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
class PersonalInfoViewModelTest {

    private val getUserNameUseCase: GetUserNameUseCase = mockk()
    private val getEmailUseCase: GetEmailUseCase = mockk()
    private val updateUserNameUseCase: UpdateUserNameUseCase = mockk()

    private lateinit var viewModel: PersonalInfoViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PersonalInfoViewModel(
            getUserNameUseCase,
            getEmailUseCase,
            updateUserNameUseCase
        )
    }

    @Test
    fun loadData_emitsUpdateDataEvent() = runTest {
        //given
        every { getUserNameUseCase() } returns "Username"
        every { getEmailUseCase() } returns "email@example.com"

        //when
        viewModel.loadData()
        advanceUntilIdle()

        //then
        assertEquals(
            PersonalInfoContract.Event.UpdateData("Username", "email@example.com"),
            viewModel.events.value
        )
    }

    @Test
    fun clickOnSave_userName_emitsSaveDoneEvent() = runTest {
        //given
        val name = "Username"
        coEvery { updateUserNameUseCase(name) } returns flowOf(ApiResult.Success(name))

        //when
        viewModel.invokeActions(PersonalInfoContract.Action.ClickOnSave(name))
        advanceUntilIdle()

        //then
        assertEquals(
            PersonalInfoContract.Event.SaveDone(name),
            viewModel.events.value
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}