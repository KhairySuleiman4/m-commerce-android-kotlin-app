package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.categories.CategoriesRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CategoriesEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class CategoriesRepoImplTest {
    private lateinit var repo: CategoriesRepoImpl

    private val categoriesRemoteDataSource: CategoriesRemoteDataSource = mockk()

    //given
    private val categoriesList = listOf(
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

    @Before
    fun setUp() {
        repo = CategoriesRepoImpl(categoriesRemoteDataSource)
        coEvery { categoriesRemoteDataSource.getCategories() } returns flowOf(
            ApiResult.Success(
                categoriesList
            )
        )
    }

    @Test
    fun getCategories_categoriesList_updateStateWithSuccess() = runTest {

        //when
        val result = repo.fetchCategories().first()

        //then
        assertThat(result, `is`(ApiResult.Success(categoriesList)))
    }
}