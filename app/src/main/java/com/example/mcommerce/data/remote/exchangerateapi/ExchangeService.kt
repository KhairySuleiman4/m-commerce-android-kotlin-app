package com.example.mcommerce.data.remote.exchangerateapi

import retrofit2.http.GET

interface ExchangeService {
    @GET("")
    fun getEGPExchangeRate()
}