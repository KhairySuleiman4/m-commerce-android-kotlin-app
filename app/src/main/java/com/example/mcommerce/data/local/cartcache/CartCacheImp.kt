package com.example.mcommerce.data.local.cartcache

import com.example.mcommerce.domain.entities.CartEntity

class CartCacheImp: CartCache {
    private var cachedCart: CartEntity? = null

    override fun getCart(): CartEntity? = cachedCart

    override fun setCart(cart: CartEntity?) {
        cachedCart = cart
    }

    override fun clear() {
        cachedCart = null
    }
}