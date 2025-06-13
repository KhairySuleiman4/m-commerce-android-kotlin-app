package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CategoriesEntity
import kotlinx.coroutines.flow.Flow

interface CategoriesRepo {
    suspend fun fetchCategories(): Flow<ApiResult<List<CategoriesEntity>>>
}