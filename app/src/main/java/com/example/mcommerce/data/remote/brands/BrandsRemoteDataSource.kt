package com.example.mcommerce.data.remote.brands

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import kotlinx.coroutines.flow.Flow

interface BrandsRemoteDataSource {
    suspend fun getBrands(): Flow<ApiResult<List<CollectionsEntity>>>
}