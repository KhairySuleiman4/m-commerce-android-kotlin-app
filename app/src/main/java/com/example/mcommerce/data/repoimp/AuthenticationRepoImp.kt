package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.mappers.toCustomerEntity
import com.example.mcommerce.data.remote.auth.firebase.Firebase
import com.example.mcommerce.data.remote.auth.shopify.CustomerRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import com.example.mcommerce.domain.repoi.AuthenticationRepo
import kotlinx.coroutines.flow.Flow

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

    override suspend fun isMeLoggedIn(): Boolean =
        firebase.isMeLoggedIn()

    override fun logout() =
        firebase.logout()

    override fun isUserVerified(): Boolean =
        firebase.isUserVerified()

    override suspend fun getCustomerAccessToken(): String =
        firebase.getCustomerAccessToken()

    override fun isGuestMode(): Boolean =
        firebase.isGuestMode()
}