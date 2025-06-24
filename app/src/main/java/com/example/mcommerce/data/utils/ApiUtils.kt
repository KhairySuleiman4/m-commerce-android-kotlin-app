package com.example.mcommerce.data.utils

import com.example.mcommerce.domain.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <T> executeAPI(api: suspend () -> T): Flow<ApiResult<T>> =
    flow {
        emit(ApiResult.Loading())
        val response = api.invoke()
        emit(ApiResult.Success(response))
    }.catch { e ->
        emit(ApiResult.Failure(e))
    }.flowOn(Dispatchers.IO)