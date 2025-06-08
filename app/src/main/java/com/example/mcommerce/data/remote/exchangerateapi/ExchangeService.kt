package com.example.mcommerce.data.remote.exchangerateapi

import com.example.mcommerce.data.models.ExchangeResponse
import retrofit2.Response
import retrofit2.http.GET

interface ExchangeService {
    @GET("latest/EGP")
    fun getEGPExchangeRate() : Response<ExchangeResponse>
}