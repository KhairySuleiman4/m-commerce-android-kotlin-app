package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.repoi.AddressesRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCustomerAddressesUseCase @Inject constructor(
    private val addressesRepo: AddressesRepo
) {
    operator fun invoke(accessToken: String): Flow<ApiResult<List<AddressEntity>?>> =
        addressesRepo.getAddresses(accessToken)
}