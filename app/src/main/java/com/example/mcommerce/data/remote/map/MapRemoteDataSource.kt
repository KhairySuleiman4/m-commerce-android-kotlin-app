package com.example.mcommerce.data.remote.map

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import kotlinx.coroutines.flow.Flow

interface MapRemoteDataSource {
    fun getAddressesByPlaceName(place: String): Flow<ApiResult<List<Triple<String, String, String>>?>>
    fun getAddressByPlaceId(placeId: String): Flow<ApiResult<AddressEntity?>>
    fun getAddressesByPlaceCoordinates(latitude: Double, longitude: Double): Flow<ApiResult<AddressEntity?>>
}