package com.example.mcommerce.presentation.auth.login

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.usecases.LoginUseCase
import com.example.mcommerce.presentation.auth.AuthContract
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

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val loginUseCase: LoginUseCase = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase)
    }

    @Test
    fun invokeActions_clickOnContinueAsGuest_triggersNavigateToHomeGuestEvent() {
        // When
        viewModel.invokeActions(AuthContract.LoginAction.ClickOnContinueAsGuest)
        // Then
        assertEquals(AuthContract.Events.NavigateToHomeGuest, viewModel.events.value)
    }

    @Test
    fun invokeActions_clickOnSignup_triggersNavigateToSignup() {
        // When
        viewModel.invokeActions(AuthContract.LoginAction.ClickOnNavigateToSignup)
        // Then
        assertEquals(AuthContract.Events.NavigateToSignup, viewModel.events.value)
    }

    @Test
    fun invokeActions_clickOnLogin_triggersNavigateToHomeUserEvent() = runTest {
        // Given
        val email = "test@example.com"
        val password = "123456"
        coEvery { loginUseCase(email, password) } returns flowOf(ApiResult.Success(true))
        // When
        viewModel.invokeActions(AuthContract.LoginAction.ClickOnLoginButton(email, password))
        advanceUntilIdle()
        // Then
        assertEquals(AuthContract.Events.NavigateToHomeUser, viewModel.events.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}