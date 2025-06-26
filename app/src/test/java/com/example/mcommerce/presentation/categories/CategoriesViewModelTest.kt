package com.example.mcommerce.presentation.categories

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CategoriesEntity
import com.example.mcommerce.domain.usecases.GetCategoriesUseCase
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

@ExperimentalCoroutinesApi
class CategoriesViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val categoriesUseCase: GetCategoriesUseCase = mockk()

    private lateinit var viewModel: CategoriesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = CategoriesViewModel(
            categoriesUseCase = categoriesUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getCategories_listOfCategories_updateStateWithSuccess() =
        runTest {
            // given
            val categoriesList = listOf(
                CategoriesEntity(
                    id = "1",
                    title = "men",
                    description = "Men Category",
                    imageUrl = "men image",
                ),
                CategoriesEntity(
                    id = "2",
                    title = "women",
                    description = "women Category",
                    imageUrl = "women image",
                )
            )
            val flow = flowOf(ApiResult.Success(categoriesList))

            coEvery { categoriesUseCase() } returns flow

            // when
            viewModel.getCategories()
            testDispatcher.scheduler.advanceUntilIdle()

            // then
            val state = viewModel.states.value
            assertTrue(state is CategoriesContract.States.Success)
            assertEquals(categoriesList, (state as CategoriesContract.States.Success).categoriesList)
        }
}