package com.example.mcommerce.presentation.products

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.domain.usecases.GetFavoriteProductsUseCase
import com.example.mcommerce.domain.usecases.GetProductsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val productsUseCase: GetProductsUseCase = mockk()

    private lateinit var viewModel: ProductsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = ProductsViewModel(
            productsUseCase = productsUseCase,
            getCurrencyUseCase = mockk(relaxed = true),
            getCurrentExchangeRateUseCase = mockk(relaxed = true),
            getFavoriteProductsUseCase = mockk(relaxed = true),
            insertProductToFavoritesUseCase = mockk(relaxed = true),
            deleteFavoriteProductUseCase = mockk(relaxed = true),
            isGuestModeUseCase = mockk(relaxed = true)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getProducts_listOfProducts_updateStateWithSuccess() =
        runTest {
            // given
            val products = listOf(
                ProductsContract.ProductUIModel(
                    id = "1",
                    title = "product1",
                    imageUrl = "product1 Image",
                    productType = "product1 type",
                    brand = "product1 Brand",
                    price = "0.0"
                ),
                ProductsContract.ProductUIModel(
                    id = "2",
                    title = "product2",
                    imageUrl = "product2 Image",
                    productType = "product2 type",
                    brand = "product2 Brand",
                    price = "0.0",
                    isFavorite = false
                )
            )
            val apiProducts = products.map {
                ProductsEntity(
                    id = it.id,
                    title = it.title,
                    imageUrl = it.imageUrl,
                    productType = it.productType,
                    brand = it.brand,
                    price = it.price,
                    isFavorite = it.isFavorite
                )
            }
            val flow = flowOf(ApiResult.Success(apiProducts))

            coEvery { productsUseCase("1") } returns flow

            // when
            viewModel.getProducts("1")
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            val state = viewModel.states.value
            assertTrue(state is ProductsContract.States.Success)
            assertEquals(2, (state as ProductsContract.States.Success).productsList.size)
        }

}