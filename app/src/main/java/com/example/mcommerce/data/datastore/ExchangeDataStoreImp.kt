package com.example.mcommerce.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ExchangeDataStoreImp(
    private val dataStore: DataStore<Preferences>,
) : ExchangeDataStore {
    private val CURRENCY = stringPreferencesKey("currency")
    private val EXCHANGERATE = doublePreferencesKey("exchange")
    private var CURRENCY_VALUE: String? = null
    private var EXCHANGERATE_VALUE: Double? = null

    override suspend fun getCurrency(): String {
        val currency : String = CURRENCY_VALUE ?: dataStore.data.map { preferences ->
                preferences[CURRENCY] ?: "EGP"
            }.first()
        CURRENCY_VALUE = currency
        return currency
    }

    override suspend fun getExchange(): Double {
        val exchange : Double = EXCHANGERATE_VALUE ?: dataStore.data.map { preferences ->
            preferences[EXCHANGERATE] ?: 1.0
        }.first()
        EXCHANGERATE_VALUE = exchange
        return exchange
    }

    override suspend fun setCurrency(value: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY] = value
            CURRENCY_VALUE = value
        }
    }

    override suspend fun setExchange(value: Double) {
        dataStore.edit { preferences ->
            preferences[EXCHANGERATE] = value
            EXCHANGERATE_VALUE = value
        }
    }
}