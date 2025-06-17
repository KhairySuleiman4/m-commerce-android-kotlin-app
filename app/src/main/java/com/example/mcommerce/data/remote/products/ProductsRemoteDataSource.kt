package com.example.mcommerce.data.remote.products

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import kotlinx.coroutines.flow.Flow

interface ProductsRemoteDataSource {
    suspend fun getProducts(id: String): Flow<ApiResult<List<ProductsEntity>>>
    suspend fun getProductById(id: String): Flow<ApiResult<ProductInfoEntity?>>
    suspend fun getAllProducts(): Flow<ApiResult<List<ProductSearchEntity>>>
}