package com.example.mcommerce.presentation.auth.signup

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import com.example.mcommerce.domain.usecases.CreateNewAccountOnFirebaseUseCase
import com.example.mcommerce.domain.usecases.CreateNewCustomerOnShopifyUseCase
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
class SignupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val createNewAccountOnFirebaseUseCase: CreateNewAccountOnFirebaseUseCase = mockk()
    private val createNewCustomerOnShopifyUseCase: CreateNewCustomerOnShopifyUseCase = mockk()
    private lateinit var viewModel: SignupViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SignupViewModel(
            shopifyUseCase = createNewCustomerOnShopifyUseCase,
            firebaseUseCase = createNewAccountOnFirebaseUseCase,
            keepMeLoggedInUseCase = mockk(relaxed = true)
        )
    }

    @Test
    fun invokeActions_clickOnContinueAsGuest_triggersNavigateToHomeGuestEvent() {
        // When
        viewModel.invokeActions(AuthContract.SignupAction.ClickOnContinueAsGuest)
        // Then
        assertEquals(AuthContract.Events.NavigateToHomeGuest, viewModel.events.value)
    }

    @Test
    fun invokeActions_clickOnLogin_triggersNavigateToLogin() {
        // When
        viewModel.invokeActions(AuthContract.SignupAction.ClickOnNavigateToLogin)
        // Then
        assertEquals(AuthContract.Events.NavigateToLogin, viewModel.events.value)
    }

    @Test
    fun invokeActions_clickOnSignup_triggersNavigateToHomeUser() = runTest {
        // Given
        val credentials = UserCredentialsEntity(
            name = "Test User",
            mail = "test@example.com",
            phoneNumber = "01012345678",
            password = "password123",
            isVerified = false,
            accessToken = ""
        )
        val confirmPassword = "password123"
        val customerId = "123456"

        coEvery {
            createNewCustomerOnShopifyUseCase(credentials)
        } returns flowOf(ApiResult.Success(customerId))
        coEvery {
            createNewAccountOnFirebaseUseCase(credentials.copy(accessToken = customerId))
        } returns flowOf(ApiResult.Success(true))
        // When
        viewModel.invokeActions(
            AuthContract.SignupAction.ClickOnSignupButton(
                credentials,
                confirmPassword
            )
        )
        advanceUntilIdle()
        // Then
        assertEquals(AuthContract.Events.NavigateToHomeUser, viewModel.events.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}