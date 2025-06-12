package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.categories.CategoriesRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CategoriesEntity
import com.example.mcommerce.domain.repoi.CategoriesRepo
import kotlinx.coroutines.flow.Flow

class CategoriesRepoImpl(private val categoriesRemoteDataSource: CategoriesRemoteDataSource): CategoriesRepo {
    override suspend fun fetchCategories(): Flow<ApiResult<List<CategoriesEntity>>> =categoriesRemoteDataSource.getCategories()
}