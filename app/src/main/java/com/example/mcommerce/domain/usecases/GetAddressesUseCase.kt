package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.repoi.MapRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAddressesUseCase @Inject constructor(private val mapRepo: MapRepo) {
    operator fun invoke(place: String): Flow<ApiResult<List<Triple<String, String, String>>?>> =
        mapRepo.getAddressesByPlaceName(place)

    fun getAddressByPlaceId(placeId: String): Flow<ApiResult<AddressEntity?>> =
        mapRepo.getAddressByPlaceId(placeId)

    operator fun invoke(latitude: Double, longitude: Double): Flow<ApiResult<AddressEntity?>> =
        mapRepo.getAddressesByPlaceCoordinates(latitude, longitude)
}