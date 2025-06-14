package com.example.mcommerce.presentation.settings.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import com.example.mcommerce.domain.usecases.SaveCurrencyUseCase
import com.example.mcommerce.presentation.settings.SettingsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val useCase: SaveCurrencyUseCase):
    ViewModel(), SettingsContract.SettingsViewModel{
    private val _states = mutableStateOf<SettingsContract.States>(SettingsContract.States.Idle)
    private val _events = mutableStateOf<SettingsContract.Events>(SettingsContract.Events.Idle)
    private var _rates: ExchangeRateEntity? = null
    override fun invokeActions(action: SettingsContract.Action) {
        when(action){
            is SettingsContract.Action.ClickOnCheckButton -> {
                _events.value = if (action.isChecked) SettingsContract.Events.UnCheckDarkMode else SettingsContract.Events.CheckDarkMode
            }
            SettingsContract.Action.ClickOnCurrency -> {
                _events.value = SettingsContract.Events.ShowCurrencyCatalog
            }
            is SettingsContract.Action.ClickOnSelectedCurrency -> {
                changeCurrency(action.currency)
                _events.value = SettingsContract.Events.SelectCurrency(action.currency)
            }
            SettingsContract.Action.ClickOnHide -> {
                _events.value = SettingsContract.Events.HideCurrencyCatalog
            }
        }
    }

    override val states: State<SettingsContract.States> get() = _states
    override val events: State<SettingsContract.Events> get() = _events
    fun showCurrency(){
        viewModelScope.launch {
            _events.value= SettingsContract.Events.SelectCurrency(useCase.getCurrentCurrency())
        }
    }

    private fun changeCurrency(currency: String){
        if (_rates!=null){
            viewModelScope.launch {
                useCase.saveCurrency(currency)
                _rates?.rates?.get(currency)?.let { useCase.saveExchangeRate(it) }
                _events.value =SettingsContract.Events.SaveCurrency(currency)
            }
        }
        else{
            getRates(currency)
        }
    }

    private fun getRates(currency: String){
        viewModelScope.launch {
            useCase.getRates().collect{
                when(it){
                    is ApiResult.Failure -> {
                        _states.value = SettingsContract.States.Failure(it.error.message.toString())
                    }
                    is ApiResult.Loading -> {
                        _states.value = SettingsContract.States.Loading
                    }
                    is ApiResult.Success -> {
                        val data=it.data ?: ExchangeRateEntity(rates = mapOf())
                        _states.value = SettingsContract.States.Success(data)
                        _rates= if(data.rates.isNotEmpty()) data else null
                        if (_rates != null) {
                            useCase.saveCurrency(currency)
                            _rates?.rates?.get(currency)?.let { useCase.saveExchangeRate(it) }
                            _events.value = SettingsContract.Events.SaveCurrency(currency)
                        }
                        else{

                        }
                    }
                }
            }
        }
    }
}