package com.example.mcommerce.presentation.profile

import com.example.mcommerce.domain.usecases.ClearLocalCartUseCase
import com.example.mcommerce.domain.usecases.GetEmailUseCase
import com.example.mcommerce.domain.usecases.GetUserNameUseCase
import com.example.mcommerce.domain.usecases.IsGuestModeUseCase
import com.example.mcommerce.domain.usecases.LogoutUseCase
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val logoutUseCase: LogoutUseCase = mockk(relaxed = true)
    private val clearLocalCartUseCase: ClearLocalCartUseCase = mockk(relaxed = true)
    private val isGuestModeUseCase: IsGuestModeUseCase = mockk()
    private val getEmailUseCase: GetEmailUseCase = mockk()
    private val getUserNameUseCase: GetUserNameUseCase = mockk()

    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(
            logoutUseCase,
            clearLocalCartUseCase,
            isGuestModeUseCase,
            getEmailUseCase,
            getUserNameUseCase
        )
    }

    @Test
    fun setupForLoggedInUser_emitsUpdateDataEvent() = runTest {
        // Given
        every { isGuestModeUseCase() } returns false
        every { getEmailUseCase() } returns "email@example.com"
        every { getUserNameUseCase() } returns "Username"

        // When
        viewModel.setup()
        advanceUntilIdle()

        // Then
        assertEquals(
            ProfileContract.Event.UpdateData(false, "email@example.com", "Username"),
            viewModel.events.value
        )
    }

    @Test
    fun setupForGuest_emitsUpdateDataEvent() = runTest {
        // Given
        every { isGuestModeUseCase() } returns true

        // When
        viewModel.setup()
        advanceUntilIdle()

        // Then
        assertEquals(
            ProfileContract.Event.UpdateData(true, "", "Guest"),
            viewModel.events.value
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}