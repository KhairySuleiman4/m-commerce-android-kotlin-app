package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import com.example.mcommerce.domain.repoi.BrandsRepo
import kotlinx.coroutines.flow.Flow

class BrandsRepoImpl(private val brandsRemoteDataSource: BrandsRemoteDataSource) : BrandsRepo {
    override suspend fun fetchBrands(): Flow<ApiResult<List<CollectionsEntity>>> =
        brandsRemoteDataSource.getBrands()
}