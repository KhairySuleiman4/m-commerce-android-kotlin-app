package com.example.mcommerce.presentation.orders

interface OrdersContract {
    data class OrderUIModel(
        val orderName: String,
        val orderDate: String,
        val orderTime: String,
        val orderPrice: String,
        val currencyCode: String,
    )
}