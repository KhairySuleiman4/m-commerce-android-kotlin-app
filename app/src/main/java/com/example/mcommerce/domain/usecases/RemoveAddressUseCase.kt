package com.example.mcommerce.domain.usecases

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.repoi.AddressesRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveAddressUseCase @Inject constructor(
    private val addressesRepo: AddressesRepo
) {
    operator fun invoke(accessToken: String, addressId: String): Flow<ApiResult<Boolean>> =
        addressesRepo.removeAddress(accessToken, addressId)
}