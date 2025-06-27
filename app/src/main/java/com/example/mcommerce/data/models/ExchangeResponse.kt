package com.example.mcommerce.data.models

import com.google.gson.annotations.SerializedName

data class ExchangeResponse(
    @SerializedName("conversion_rates")
    val rates: Map<String, Double>
)