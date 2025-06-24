package com.example.mcommerce.data.models

data class OrderModel(
    val orderId: String,
    val orderName: String,
    val orderDate: String,
    val orderTime: String,
    val orderPrice: String,
    val currencyCode: String,
    val productTitle: String,
    val productQuantity: String,
    val productPrice: String,
    val productImage: String,
    val variantTitle: String,
    val variantImage: String,
    val customerUrl: String,
    val customerName: String,
    val customerPhone: String,
    val shippingAddress: String,
    val shippingCity: String
)