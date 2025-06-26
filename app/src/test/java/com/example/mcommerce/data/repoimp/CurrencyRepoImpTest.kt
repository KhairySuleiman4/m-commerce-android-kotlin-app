package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.datastore.ExchangeDataStore
import com.example.mcommerce.data.remote.currency.CurrencyRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ExchangeRateEntity
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CurrencyRepoImpTest {

    private lateinit var repo: CurrencyRepoImp
    private val local: ExchangeDataStore = mockk()
    private val remote: CurrencyRemoteDataSource = mockk()

    private val expectedExchangeRate = ExchangeRateEntity(
        mapOf(pairs = arrayOf(Pair("USD",0.1),Pair("EGP",1.0)))
    )

    @Before
    fun setup() {
        repo = CurrencyRepoImp(local, remote)
    }

    @Test
    fun getCurrencyFromRemoteSource_returnsSuccessExchangeRate() = runTest {
        //given
        val expected = ApiResult.Success<ExchangeRateEntity?>(expectedExchangeRate)
        coEvery { remote.getCurrency() } returns flowOf(expected)

        //when
        val result = repo.getCurrencyFromRemoteSource().first()

        //then
        assertEquals(expected, result)
    }

    @Test
    fun getCurrencyValueFromLocalSource_returnsCurrencyCode() = runTest {
        //given
        val expected = "USD"
        coEvery { local.getCurrency() } returns expected

        //when
        val result = repo.getCurrencyValueFromLocalSource()

        //then
        assertEquals(expected, result)
    }

    @Test
    fun getExchangeValueFromLocalSource_returnsExchangeRate() = runTest {
        //given
        val expected = 30.0
        coEvery { local.getExchange() } returns expected

        //when
        val result = repo.getExchangeValueFromLocalSource()

        //then
        assertEquals(expected, result)
    }
}