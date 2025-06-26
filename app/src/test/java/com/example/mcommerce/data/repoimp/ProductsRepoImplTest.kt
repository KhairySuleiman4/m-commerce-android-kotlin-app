package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.products.ProductsRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductVariantEntity
import com.example.mcommerce.domain.entities.ProductsEntity
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

class ProductsRepoImplTest {

    private lateinit var repo: ProductsRepoImpl
    private val productsRemoteDataSource: ProductsRemoteDataSource = mockk()
    private val productId = "product123"
    private val dummyProduct = ProductInfoEntity(
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
    fun getProductsByCollection_productsList_updateStateWithSuccess() = runTest {
        //given
        val productsList = listOf(
            ProductsEntity(
                id = "1",
                title = "product1",
                imageUrl = "product1 Image",
                productType = "product1 type",
                brand = "product1 Brand",
                isFavorite = false,
                price = "0.0"
            ),
            ProductsEntity(
                id = "2",
                title = "product2",
                imageUrl = "product2 Image",
                productType = "product2 type",
                brand = "product2 Brand",
                isFavorite = false,
                price = "0.0"
            )
        )
        val flow = flowOf(ApiResult.Success(productsList))
        coEvery { productsRemoteDataSource.getProducts("1") } returns flow

        //when
        val result = repo.fetchProductsByCollection("1").first()

        //then
        assertTrue(result is ApiResult.Success)
        assertThat(result, `is`(ApiResult.Success(productsList)))
    }

    @Test
    fun getProductsById_productId_updateStateWithSuccess() = runTest {
        //given
        val product = ProductInfoEntity(
            id = "1",
            images = listOf("productImage1", "productImage2"),
            title = "product1 title",
            price = 0.0,
            priceUnit = "EGP",
            productType = "product1 type",
            vendor = "vendor name",
            description = "product1 description",
            variants = listOf(
                ProductVariantEntity(
                    id = "1",
                    imageUrl = "variant image",
                    title = "variant title",
                    price = "0.0",
                ),
                ProductVariantEntity(
                    id = "2",
                    imageUrl = "variant image",
                    title = "variant title",
                    price = "0.0",
                )
            ),
            isFavorite = false
        )
        coEvery { productsRemoteDataSource.getProductById("1") } returns flowOf(ApiResult.Success(product))

        //when
        val result = repo.fetchProductById("1").first()

        //then
        assertTrue(result is ApiResult.Success)
        assertThat(result, `is`(ApiResult.Success(product)))
    }

    @Test
    fun fetchProductById_dummyProductId_successWithProductInfo() = runTest {
        // Given
        coEvery { productsRemoteDataSource.getProductById(productId) } returns flowOf(ApiResult.Success(dummyProduct))
        // When
        val result = repo.fetchProductById(productId).first()
        // Then
        assertThat(result, `is`(ApiResult.Success(dummyProduct)))
    }
}