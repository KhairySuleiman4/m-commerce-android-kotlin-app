package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.local.cartcache.CartCache
import com.example.mcommerce.data.remote.auth.firebase.Firebase
import com.example.mcommerce.data.remote.cart.CartRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CartEntity
import com.example.mcommerce.domain.entities.LineEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CartRepoImpTest {
    private lateinit var repo: CartRepoImp

    private val remoteDataSource: CartRemoteDataSource = mockk()
    private val firebase: Firebase = mockk()
    private val cache: CartCache = mockk()
    private val cart = CartEntity(
        id = "id",
        checkout = "checkout url",
        subtotalAmount = 0.0,
        totalAmount = 0.0,
        discountAmount = 0.0,
        items = listOf(
            LineEntity(
                id = "item id",
                quantity = 1,
                price = 0.0,
                image = "item image",
                title = "item title",
                category = "item category",
                brand = "item brand",
                lineId = "line item id"
            )
        )
    )

    @Before
    fun setup(){
        every { firebase.isMeLoggedIn() } returns true
        every { firebase.isUserVerified() } returns true
        every { cache.getCart() } returns cart
        repo = CartRepoImp(remoteDataSource, firebase, cache)
    }

    @Test
    fun getCart_userLoggedInVerifiedAndHasCartInCache_returnsSuccess() = runTest {
        coEvery { firebase.getCustomerAccessToken() } returns """{"token":"access token"}"""
        // When
        val result = repo.getCart().first()

        // Then
        assertEquals(ApiResult.Success(cart), result)
    }

    @Test
    fun addItemToCart_itemNotInCart_returnsSuccessNewCart() = runTest {
        //given
        val itemId = "new-item-id"
        val updatedCart = cart.copy(
            items = cart.items + LineEntity(
                id = itemId,
                quantity = 1,
                price = 100.0,
                image = "image",
                title = "title",
                category = "category",
                brand = "brand",
                lineId = "new-line-id"
            )
        )
        val cartWithoutItem = cart.copy(items = emptyList())
        every { cache.getCart() } returns cartWithoutItem
        every { cache.setCart(updatedCart) } returns Unit
        coEvery {
            remoteDataSource.addItemToCart(cartWithoutItem.id, 1, itemId)
        } returns kotlinx.coroutines.flow.flowOf(ApiResult.Success(updatedCart))

        //when
        val result = repo.addItemToCart(itemId, 1).last()

        //then
        assertEquals(ApiResult.Success(updatedCart), result)
    }

    @Test
    fun removeItemFromCart_itemExists_removesSuccessfully_returnsSuccess() = runTest {
        //given
        val itemLineId = "line item id"
        val updatedCart = cart.copy(items = emptyList())
        every { cache.setCart(updatedCart) } returns Unit
        coEvery {
            remoteDataSource.removeItemFromCart(cart.id, itemLineId)
        } returns kotlinx.coroutines.flow.flowOf(ApiResult.Success(updatedCart))

        //when
        val result = repo.removeItemFromCart(itemLineId).last()

        //then
        assertEquals(ApiResult.Success(updatedCart), result)
    }

}