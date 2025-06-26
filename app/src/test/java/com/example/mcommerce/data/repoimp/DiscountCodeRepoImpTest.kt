package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.discount.DiscountsRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DiscountCodeRepoImpTest {
    private lateinit var repo: DiscountCodeRepoImp
    private val discountsRemoteDataSource:DiscountsRemoteDataSource = mockk()

    @Before
    fun setup(){
        repo = DiscountCodeRepoImp(discountsRemoteDataSource)
    }

    @Test
    fun getCodes_returnSuccessListOfString() = runTest{
        //given
        val expected = ApiResult.Success<List<String?>?>(listOf("code1", "code2"))
        coEvery { discountsRemoteDataSource.getDiscountCodes() } returns flowOf(expected)

        //when
        val result = repo.getCodes().first()

        //then
        assertEquals(expected, result)
    }
}