package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import kotlinx.coroutines.flow.Flow

interface MapRepo {
    fun getAddressesByPlaceName(place: String): Flow<ApiResult<List<AddressEntity>?>>
    fun getAddressesByPlaceCoordinates(latitude: Double, longitude: Double): Flow<ApiResult<AddressEntity?>>
}