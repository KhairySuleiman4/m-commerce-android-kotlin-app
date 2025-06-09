package com.example.mcommerce.data.remote.products

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductsEntity
import kotlinx.coroutines.flow.Flow

interface ProductsRemoteDataSource {
    suspend fun getProducts(id: String): Flow<ApiResult<List<ProductsEntity>>>
}