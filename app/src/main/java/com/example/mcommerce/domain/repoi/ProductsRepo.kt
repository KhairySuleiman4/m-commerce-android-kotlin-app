package com.example.mcommerce.domain.repoi

import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.type.ProductSortKeys
import kotlinx.coroutines.flow.Flow

interface ProductsRepo {
    suspend fun fetchProductsByCollection(id: String): Flow<ApiResult<List<ProductsEntity>>>
    suspend fun fetchProductById(id: String): Flow<ApiResult<ProductInfoEntity?>>
    suspend fun fetchAllProducts(): Flow<ApiResult<List<ProductSearchEntity>>>
    suspend fun fetchHomeProducts(sortKeys: ProductSortKeys, reverse: Boolean): Flow<ApiResult<List<ProductsEntity>>>
    suspend fun insertProductToFavorites(product: ProductSearchEntity)
    suspend fun getFavoriteProducts(): Flow<ApiResult<List<ProductSearchEntity>>>
    suspend fun deleteFavoriteProduct(id: String)
}