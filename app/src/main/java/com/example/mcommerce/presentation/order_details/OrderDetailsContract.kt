package com.example.mcommerce.presentation.order_details

interface OrderDetailsContract {

    data class OrderDetailsUIModel(
        val name: String,
        val date: String,
        val time: String,
        val totalPrice: String,
        val subtotalPrice: String,
        val discount: String,
        val currencyCode: String,
        val userAddress: String,
        val userCity: String,
        val userName: String,
        val userPhone: String,
        // val imageUrl: String,
    )
}