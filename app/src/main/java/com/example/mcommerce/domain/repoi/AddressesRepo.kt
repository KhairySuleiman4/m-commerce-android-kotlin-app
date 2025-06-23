package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import kotlinx.coroutines.flow.Flow

interface AddressesRepo {
    fun addAddress(accessToken: String, address: AddressEntity): Flow<ApiResult<Boolean>>
    fun removeAddress(accessToken: String, addressId: String): Flow<ApiResult<Boolean>>
    fun getAddresses(accessToken: String): Flow<ApiResult<List<AddressEntity>?>>
    fun updateDefault(accessToken: String, addressId: String): Flow<ApiResult<Boolean>>
}