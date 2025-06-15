package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import kotlinx.coroutines.flow.Flow

interface ProductsRepo {
    suspend fun fetchProductsByBrand(id: String): Flow<ApiResult<List<ProductsEntity>>>
    suspend fun fetchProductById(id: String): Flow<ApiResult<ProductInfoEntity>>
}