package com.example.mcommerce.data.datastore

interface ExchangeDataStore {
    suspend fun getCurrency(): String
    suspend fun getExchange(): Double
    suspend fun setCurrency(value: String)
    suspend fun setExchange(value: Double)
}