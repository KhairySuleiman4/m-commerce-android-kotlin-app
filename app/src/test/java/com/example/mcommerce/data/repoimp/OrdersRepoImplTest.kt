package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.orders.OrdersRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.LineItemEntity
import com.example.mcommerce.domain.entities.OrderEntity
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class OrdersRepoImplTest {
    private lateinit var repo: OrdersRepoImpl
    private val ordersRemoteDataSource: OrdersRemoteDataSource = mockk()

    //given
    private val ordersList = listOf(
        OrderEntity(
            name = "1024",
            processedAt = "2025-06-25T17:32:25Z",
            subtotalPrice = "269.95",
            totalPrice = "307.74",
            shippingAddress = "Ahmed Abou Zaid Road",
            city = "Ismailia Governorate",
            customerName = "khairy",
            phone = "+201257798654",
            lineItems = listOf(
                LineItemEntity(
                    quantity = "1",
                    variantTitle = "4 / black",
                    productId = "1",
                    productTitle = "t-shirt",
                    price = "140.0",
                    imageUrl = "t-shirt image"
                )
            )
        ),
        OrderEntity(
            name = "1024",
            processedAt = "2025-06-25T17:32:25Z",
            subtotalPrice = "269.95",
            totalPrice = "307.74",
            shippingAddress = "Ahmed Abou Zaid Road",
            city = "Ismailia Governorate",
            customerName = "khairy",
            phone = "+201257798654",
            lineItems = listOf(
                LineItemEntity(
                    quantity = "1",
                    variantTitle = "4 / black",
                    productId = "1",
                    productTitle = "t-shirt",
                    price = "140.0",
                    imageUrl = "t-shirt image"
                )
            )
        )
    )

    @Before
    fun setUp() {
        repo = OrdersRepoImpl(ordersRemoteDataSource)
        coEvery { ordersRemoteDataSource.getOrders("1") } returns flowOf(
            ApiResult.Success(
                ordersList
            )
        )
    }

    @Test
    fun getOrders_ordersList_updateStateWithSuccess() = runTest {

        //when
        val result = repo.getOrders("1").first()

        //then
        assertTrue(result is ApiResult.Success)
        assertThat(result, `is`(ApiResult.Success(ordersList)))
    }
}