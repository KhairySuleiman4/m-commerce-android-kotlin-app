package com.example.mcommerce.data.remote.auth.firebase

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import kotlinx.coroutines.flow.Flow

interface Firebase {
    suspend fun createNewAccount(credentials: UserCredentialsEntity): Flow<ApiResult<Boolean>>
    suspend fun login(email: String, password: String): Flow<ApiResult<Boolean>>
    suspend fun isMeLoggedIn(): Flow<ApiResult<Boolean>>
    fun logout()
    fun isUserVerified(): Flow<ApiResult<Boolean>>
    fun getCustomerAccessToken(): Flow<ApiResult<String>>
    fun isGuestMode(): Flow<ApiResult<Boolean>>
    suspend fun insertProductToFavorites(product: ProductSearchEntity)
    suspend fun getFavoriteProducts(): Flow<ApiResult<List<ProductSearchEntity>>>
    suspend fun deleteProduct(id: String)
}