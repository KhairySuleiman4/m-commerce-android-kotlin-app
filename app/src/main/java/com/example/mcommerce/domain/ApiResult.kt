package com.example.mcommerce.domain

sealed interface ApiResult<T> {
    class Loading<T>: ApiResult<T>
    data class Success<T>(val data: T): ApiResult<T>
    data class Failure<T>(val error: Throwable): ApiResult<T>
}