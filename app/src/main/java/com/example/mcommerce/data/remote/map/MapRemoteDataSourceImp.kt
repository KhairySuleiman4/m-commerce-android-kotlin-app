package com.example.mcommerce.data.remote.map

import android.location.Geocoder
import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MapRemoteDataSourceImp(
    private val placesClient: PlacesClient,
    private val geocoder: Geocoder
    ): MapRemoteDataSource {

    override fun getAddressesByPlaceName(place: String): Flow<ApiResult<List<Triple<String, String, String>>?>> = executeAPI {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(place)
            .build()
            val response = placesClient.findAutocompletePredictions(request).await()
            response.autocompletePredictions.map {
                Triple(
                    it.placeId,
                    it.getPrimaryText(null).toString(),
                    it.getSecondaryText(null).toString()
                )
            }
    }

    override fun getAddressByPlaceId(placeId: String): Flow<ApiResult<AddressEntity?>> = executeAPI {
        val placeRequest = FetchPlaceRequest.builder(
            placeId,
            listOf(
                Place.Field.DISPLAY_NAME,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG,
                Place.Field.FORMATTED_ADDRESS
            )
        ).build()

        val place = placesClient.fetchPlace(placeRequest).await().place
        val components = place.addressComponents?.asList() ?: emptyList()

        val country = components.find { it.types.contains("country") }?.name ?: ""
        val postalCode = components.find { it.types.contains("postal_code") }?.name ?: ""
        val subName = components.find { it.types.contains("locality") || it.types.contains("administrative_area_level_1") }?.name ?: ""

        val latLng = place.latLng

        AddressEntity(
            id = "",
            name = place.displayName ?: "",
            subName = place.formattedAddress ?: "",
            country = country,
            city = subName,
            zip = postalCode,
            latitude = latLng?.latitude ?: 0.0,
            longitude = latLng?.longitude ?: 0.0
        )
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