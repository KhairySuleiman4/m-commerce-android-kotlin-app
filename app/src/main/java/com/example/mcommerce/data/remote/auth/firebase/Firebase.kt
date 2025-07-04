package com.example.mcommerce.data.remote.auth.firebase

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import kotlinx.coroutines.flow.Flow

interface Firebase {
    suspend fun createNewAccount(credentials: UserCredentialsEntity): Flow<ApiResult<Boolean>>
    suspend fun login(email: String, password: String): Flow<ApiResult<Boolean>>
    suspend fun updatePhoto(value: String): Flow<ApiResult<Boolean>>
    fun updateName(name: String): Flow<ApiResult<String>>
    fun isMeLoggedIn(): Boolean
    fun logout()
    fun isUserVerified(): Boolean
    suspend fun getCustomerAccessToken(): String
    fun getEmail(): String
    fun getName(): String
    fun isGuestMode(): Boolean
    suspend fun insertProductToFavorites(product: ProductSearchEntity)
    suspend fun getFavoriteProducts(): Flow<ApiResult<List<ProductSearchEntity>>>
    suspend fun deleteProduct(id: String)
}