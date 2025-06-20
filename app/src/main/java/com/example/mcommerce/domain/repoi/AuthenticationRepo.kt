package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepo {
    suspend fun createAccountOnShopify(credentials: UserCredentialsEntity): Flow<ApiResult<String>>
    suspend fun createAccountOnFirebase(credentials: UserCredentialsEntity): Flow<ApiResult<Boolean>>
    suspend fun login(email: String, password: String): Flow<ApiResult<Boolean>>
    suspend fun isMeLoggedIn(): Boolean
    fun logout()
    fun isUserVerified(): Boolean
    suspend fun getCustomerAccessToken(): String
    fun isGuestMode(): Boolean
}