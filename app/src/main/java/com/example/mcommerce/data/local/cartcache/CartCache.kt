package com.example.mcommerce.data.local.cartcache

import com.example.mcommerce.domain.entities.CartEntity

interface CartCache {
    fun getCart(): CartEntity?
    fun setCart(cart: CartEntity?)
    fun clear()
}