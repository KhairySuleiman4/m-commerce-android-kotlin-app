package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.products.ProductsRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class ProductsRepoImpTest {

    private lateinit var repo: ProductsRepoImpl
    private val productsRemoteDataSource: ProductsRemoteDataSource = mockk()
    private val productId = "product123"
    private val productInfo = ProductInfoEntity(
        id = productId,
        images = listOf(),
        title = "Sample Product",
        price = 100.0,
        priceUnit = "EGP",
        productType = "SHOES",
        vendor = "VENDOR",
        description = "Product Description",
        variants = listOf(),
        isFavorite = false
    )

    @Before
    fun setUp() {
        repo = ProductsRepoImpl(productsRemoteDataSource)
    }

    @Test
    fun fetchProductById_dummyProductId_successWithProductInfo() = runTest {
        // Given
        coEvery { productsRemoteDataSource.getProductById(productId) } returns flowOf(ApiResult.Success(productInfo))
        // When
        val result = repo.fetchProductById(productId).first()
        // Then
        assertThat(result, `is`(ApiResult.Success(productInfo)))
    }
}