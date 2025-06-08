package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.repoi.AppRepo
import kotlinx.coroutines.flow.Flow

class AppRepoImp(private val brandsRemoteDataSource: BrandsRemoteDataSource): AppRepo {
    override suspend fun fetchBrands(): Flow<ApiResult<List<CollectionsEntity>>> = brandsRemoteDataSource.getBrands()
}