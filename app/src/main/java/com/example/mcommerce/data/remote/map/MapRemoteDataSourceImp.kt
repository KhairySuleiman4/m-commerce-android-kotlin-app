package com.example.mcommerce.data.remote.map

import android.location.Geocoder
import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MapRemoteDataSourceImp(private val geocoder: Geocoder): MapRemoteDataSource {

    override fun getAddressesByPlaceName(place: String): Flow<ApiResult<List<AddressEntity>?>> = flow {
        emit(ApiResult.Loading())
        try {
            val value = withContext(Dispatchers.IO) {
                val x=geocoder.getFromLocationName(place, 5)
                    x?.map { it.toModel() }
                    ?.map { it.toEntity() }
            }
            emit(ApiResult.Success(value))
        } catch (e: Exception) {
           emit(ApiResult.Failure(e))
        }
    }

    override fun getAddressesByPlaceCoordinates(
        latitude: Double,
        longitude: Double
    ): Flow<ApiResult<AddressEntity?>> = flow {
        emit(ApiResult.Loading())
        try {
            val value = withContext(Dispatchers.IO) {
                geocoder.getFromLocation(latitude, longitude, 1)
                    ?.map { it.toModel() }
                    ?.map { it.toEntity() }
            }
            emit(ApiResult.Success(value?.get(0)))
        } catch (e: Exception) {
            emit(ApiResult.Failure(e))
        }
    }
}