package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.map.MapRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.repoi.MapRepo
import kotlinx.coroutines.flow.Flow

class MapRepoImp(private val mapRemoteDataSource: MapRemoteDataSource) : MapRepo {
    override fun getAddressesByPlaceName(place: String): Flow<ApiResult<List<Triple<String, String, String>>?>> =
        mapRemoteDataSource.getAddressesByPlaceName(place)

    override fun getAddressByPlaceId(placeId: String): Flow<ApiResult<AddressEntity?>> =
        mapRemoteDataSource.getAddressByPlaceId(placeId)

    override fun getAddressesByPlaceCoordinates(
        latitude: Double,
        longitude: Double
    ): Flow<ApiResult<AddressEntity?>> =
        mapRemoteDataSource.getAddressesByPlaceCoordinates(latitude, longitude)
}