package com.example.mcommerce.data.remote.categories

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CategoriesEntity
import kotlinx.coroutines.flow.Flow

interface CategoriesRemoteDataSource {
    suspend fun getCategories(): Flow<ApiResult<List<CategoriesEntity>>>
}