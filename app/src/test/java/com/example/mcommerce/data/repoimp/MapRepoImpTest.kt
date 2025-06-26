package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.map.MapRemoteDataSource
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

class MapRepoImpTest {
    private lateinit var repo: MapRepoImp

    private val mapRemoteDataSource: MapRemoteDataSource = mockk()

    @Before
    fun setup() {
        repo = MapRepoImp(mapRemoteDataSource)
    }

    @Test
    fun getAddressesByPlaceName_searchQuery_successResultOfAddresses() = runTest {
        //given
        val place = "Cairo"
        val expected = ApiResult.Success<List<Triple<String, String, String>>?>(
            listOf(
                Triple(
                    "Place id",
                    "Place Name",
                    "Country"
                )
            )
        )
        coEvery { mapRemoteDataSource.getAddressesByPlaceName(place) } returns flowOf(expected)

        //when
        val result = repo.getAddressesByPlaceName(place).first()

        //then
        assertEquals(expected, result)
    }

    @Test
    fun getAddressByPlaceId_placeId_successResultOfAddress() = runTest {
        //given
        val placeId = "abc123"
        val expected = ApiResult.Success<AddressEntity?>(
            AddressEntity(
                id = "Id",
                name = "Street",
                subName = "Area",
                country = "Country",
                city = "City",
                zip = "ZIP",
                latitude = 0.0,
                longitude = 0.0,
            )
        )
        coEvery { mapRemoteDataSource.getAddressByPlaceId(placeId) } returns flowOf(expected)

        //when
        val result = repo.getAddressByPlaceId(placeId).first()

        //then
        assertEquals(expected, result)
    }

    @Test
    fun getAddressesByPlaceCoordinates_latitudeAndLongitude_successResultOfAddress() = runTest {
        //given
        val lat = 30.0
        val lng = 31.0
        val expected = ApiResult.Success<AddressEntity?>(
            AddressEntity(
                id = "Id",
                name = "Street",
                subName = "Area",
                country = "Country",
                city = "City",
                zip = "ZIP",
                latitude = lat,
                longitude = lng,
            )
        )
        coEvery { mapRemoteDataSource.getAddressesByPlaceCoordinates(lat, lng) } returns flowOf(
            expected
        )

        //when
        val result = repo.getAddressesByPlaceCoordinates(lat, lng).first()

        //then
        assertEquals(expected, result)
    }
}