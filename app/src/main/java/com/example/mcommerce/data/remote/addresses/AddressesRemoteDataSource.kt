package com.example.mcommerce.data.remote.addresses

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import kotlinx.coroutines.flow.Flow

interface AddressesRemoteDataSource {
    fun addAddress(
        accessToken: String,
        address: AddressEntity,
        name: String
    ): Flow<ApiResult<Boolean>>

    fun removeAddress(accessToken: String, addressId: String): Flow<ApiResult<Boolean>>
    fun getAddresses(accessToken: String): Flow<ApiResult<List<AddressEntity>?>>
    fun updateDefault(accessToken: String, addressId: String): Flow<ApiResult<Boolean>>
    fun checkForDefault(accessToken: String): Flow<ApiResult<Boolean>>
}