package com.example.mcommerce.data.remote.addresses

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import kotlinx.coroutines.flow.Flow

class AddressesRemoteDataSourceImp(
    private val graphQLService: GraphQLService
) : AddressesRemoteDataSource {
    override fun addAddress(accessToken: String, address: AddressEntity, name: String): Flow<ApiResult<Boolean>> = executeAPI {
        val result = graphQLService.addAddress(accessToken, address.toModel(), name).data
        result?.customerAddressCreate?.customerAddress?.id != null
    }

    override fun removeAddress(accessToken: String, addressId: String): Flow<ApiResult<Boolean>> = executeAPI {
        val result = graphQLService.removeAddress(accessToken,addressId).data
        result?.customerAddressDelete?.deletedCustomerAddressId != null
    }

    override fun getAddresses(accessToken: String): Flow<ApiResult<List<AddressEntity>?>> = executeAPI {
      val x =  graphQLService.getAddresses(accessToken).data
            x?.toModel()?.map { it.toEntity() }
    }

    override fun updateDefault(accessToken: String, addressId: String): Flow<ApiResult<Boolean>> = executeAPI {
        val result = graphQLService.changeDefaultAddress(accessToken,addressId).data
        result?.customerDefaultAddressUpdate?.customer?.id != null
    }

    override fun checkForDefault(accessToken: String): Flow<ApiResult<Boolean>> = executeAPI {
        val result = graphQLService.checkForDefaultAddress(accessToken).data
        result?.customer?.defaultAddress?.id != null
    }
}