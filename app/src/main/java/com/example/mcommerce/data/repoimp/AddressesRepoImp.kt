package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.addresses.AddressesRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.repoi.AddressesRepo
import kotlinx.coroutines.flow.Flow

class AddressesRepoImp(
    private val addressesRemoteDataSource: AddressesRemoteDataSource
) : AddressesRepo {
    override fun addAddress(
        accessToken: String,
        address: AddressEntity,
        name: String
    ): Flow<ApiResult<Boolean>> = addressesRemoteDataSource.addAddress(accessToken, address, name)

    override fun removeAddress(accessToken: String, addressId: String): Flow<ApiResult<Boolean>> =
        addressesRemoteDataSource.removeAddress(accessToken, addressId)

    override fun getAddresses(accessToken: String): Flow<ApiResult<List<AddressEntity>?>> =
        addressesRemoteDataSource.getAddresses(accessToken)

    override fun updateDefault(accessToken: String, addressId: String): Flow<ApiResult<Boolean>> =
        addressesRemoteDataSource.updateDefault(accessToken, addressId)

    override fun checkForDefault(accessToken: String): Flow<ApiResult<Boolean>> =
        addressesRemoteDataSource.checkForDefault(accessToken)
}