package com.example.mcommerce.presentation.settings.viewmodel

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import com.example.mcommerce.domain.usecases.SaveCurrencyUseCase
import com.example.mcommerce.presentation.settings.SettingsContract
import io.mockk.coEvery
import io.mockk.coVerify
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
class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private val useCase: SaveCurrencyUseCase = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel(useCase)
    }

    @Test
    fun clickOnCheckButton_checkedTrue_emitsUnCheckDarkMode() {
        viewModel.invokeActions(SettingsContract.Action.ClickOnCheckButton(isChecked = true))

        assertEquals(
            SettingsContract.Events.UnCheckDarkMode,
            viewModel.events.value
        )
    }

    @Test
    fun clickOnSelectedCurrency_withRates_emitsSaveCurrencyEvent() = runTest {
        //given
        val currency = "USD"
        val rateValue = 30.0
        val ratesMap = mapOf(currency to rateValue)
        val rateEntity = ExchangeRateEntity(ratesMap)
        coEvery { useCase.getRates() } returns flowOf(ApiResult.Success(rateEntity))

        //when
        viewModel.invokeActions(SettingsContract.Action.ClickOnSelectedCurrency(currency))
        advanceUntilIdle()

        //then
        coVerify { useCase.saveCurrency(currency) }
        coVerify { useCase.saveExchangeRate(rateValue) }
        assertEquals(SettingsContract.Events.SaveCurrency(currency), viewModel.events.value)
    }

    @Test
    fun clickOnSelectedCurrency_withoutRates_fetchesAndSavesOnSuccess() = runTest {
        //given
        val currency = "USD"
        val rateValue = 30.0
        val ratesMap = mapOf(currency to rateValue)
        val exchangeRateEntity = ExchangeRateEntity( rates = ratesMap)
        coEvery { useCase.getRates() } returns flowOf(ApiResult.Success(exchangeRateEntity))

        //when
        viewModel.invokeActions(SettingsContract.Action.ClickOnSelectedCurrency(currency))
        advanceUntilIdle()

        //then
        assertEquals(
            SettingsContract.States.Success(exchangeRateEntity),
            viewModel.states.value
        )
        assertEquals(
            SettingsContract.Events.SaveCurrency(currency),
            viewModel.events.value
        )
        coVerify { useCase.saveCurrency(currency) }
        coVerify { useCase.saveExchangeRate(rateValue) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}