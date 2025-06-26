package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.addresses.AddressesRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddressesRepoImpTest {

    private lateinit var repo: AddressesRepoImp

    private val addressesRemoteDataSource: AddressesRemoteDataSource = mockk()

    private val accessToken = "accessToken"

    @Before
    fun setup(){
        repo = AddressesRepoImp(addressesRemoteDataSource)
    }

    @Test
    fun addAddress_customerAccessTokenAndAddressAndCustomerName_successResultOfTrue() = runTest {
        //given
        val address = AddressEntity(
            id = "Id",
            name = "Street",
            subName = "Area",
            country = "Country",
            city = "City",
            zip = "ZIP",
            latitude = 0.0,
            longitude = 0.0,
        )
        val name = "customer name"
        val expected = ApiResult.Success(true)
        coEvery { addressesRemoteDataSource.addAddress(accessToken, address, name) } returns flowOf(expected)

        //when
        val result = repo.addAddress(accessToken, address, name).first()

        //then
        assertEquals(expected, result)

    }

    @Test
    fun removeAddress_customerAccessTokenAndAddressId_successResultOfTrue() = runTest {
        //given
        val addressId = "id"
        val expected = ApiResult.Success(true)
        coEvery { addressesRemoteDataSource.removeAddress(accessToken, addressId) } returns flowOf(expected)

        //when
        val result = repo.removeAddress(accessToken, addressId).first()

        //then
        assertEquals(expected, result)

    }

    @Test
    fun getAddresses_customerAccessToken_successResultOfAddressesList() = runTest {
        //given
        val expected = ApiResult.Success<List<AddressEntity>?>(listOf(
            AddressEntity(
                id = "Id 1",
                name = "Street 1",
                subName = "Area 1",
                country = "Country 1",
                city = "City 1",
                zip = "ZIP 1",
                latitude = 0.0,
                longitude = 0.0,
                isDefault = false
            ),
            AddressEntity(
                id = "Id 2",
                name = "Street 2",
                subName = "Area 2",
                country = "Country 2",
                city = "City 2",
                zip = "ZIP 2",
                latitude = 1.0,
                longitude = 1.0,
                isDefault = true
            )
        ))
        coEvery { addressesRemoteDataSource.getAddresses(accessToken) } returns flowOf(expected)

        //when
        val result = repo.getAddresses(accessToken).first()

        //then
        assertEquals(expected, result)

    }

}