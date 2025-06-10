package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import kotlinx.coroutines.flow.Flow

interface BrandsRepo {
    suspend fun fetchBrands(): Flow<ApiResult<List<CollectionsEntity>>>
}