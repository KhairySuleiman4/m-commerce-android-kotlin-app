package com.example.mcommerce.data.utils

import com.example.mcommerce.domain.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

suspend fun <T> executeAPI(api: suspend () -> T): Flow<ApiResult<T>> = flow {
    try {
        emit(ApiResult.Loading())
        val response = api.invoke()
        emit(ApiResult.Success(response))
    } catch (e: Exception) {
        emit(ApiResult.Failure(e))
    }
}