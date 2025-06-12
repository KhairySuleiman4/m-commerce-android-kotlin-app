package com.example.mcommerce.data.remote.auth.firebase

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.UserCredentialsEntity
import kotlinx.coroutines.flow.Flow

interface Firebase {
    suspend fun createNewAccount(credentials: UserCredentialsEntity): Flow<ApiResult<Boolean>>
}