package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.mappers.toCustomerEntity
import com.example.mcommerce.data.remote.auth.firebase.Firebase
import com.example.mcommerce.data.remote.auth.shopify.CustomerRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import com.example.mcommerce.domain.repoi.AuthenticationRepo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthenticationRepoImp(
    private val firebase: Firebase,
    private val remote: CustomerRemoteDataSource
): AuthenticationRepo {
    override suspend fun createAccountOnShopify(credentials: UserCredentialsEntity): Flow<ApiResult<String>> =
        remote.createCustomer(customer = credentials.toCustomerEntity())

    override suspend fun createAccountOnFirebase(credentials: UserCredentialsEntity): Flow<ApiResult<Boolean>> =
        firebase.createNewAccount(credentials)

    override suspend fun login(email: String, password: String): Flow<ApiResult<Boolean>> =
        firebase.login(email, password)
}