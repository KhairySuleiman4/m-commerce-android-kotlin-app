package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class BrandsRepoImpTest {
    private lateinit var repo: BrandsRepoImpl

    private val brandsRemoteDataSource: BrandsRemoteDataSource = mockk()

    //given
    private val collectionsList = listOf(
        CollectionsEntity(
            id = "1",
            title = "Adidas",
            imageUrl = "AdidasImage"
        ),
        CollectionsEntity(
            id = "2",
            title = "Nike",
            imageUrl = "NikeImage"
        )
    )

    @Before
    fun setUp() {
        repo = BrandsRepoImpl(brandsRemoteDataSource)
        coEvery { brandsRemoteDataSource.getBrands() } returns flowOf(
            ApiResult.Success(
                collectionsList
            )
        )
    }

    @Test
    fun getBrands_collectionsList_listOfBrands() = runTest {

        //when
        val result = repo.fetchBrands().first()

        //then
        assertThat(result, `is`(ApiResult.Success(collectionsList)))
    }

}