package com.example.mcommerce.data.repoimp

import com.example.mcommerce.data.remote.products.ProductsRemoteDataSource
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.domain.repoi.ProductsRepo
import com.example.mcommerce.type.ProductSortKeys
import kotlinx.coroutines.flow.Flow

class ProductsRepoImpl(private val productsRemoteDataSource: ProductsRemoteDataSource): ProductsRepo {
    override suspend fun fetchProductsByCollection(id: String): Flow<ApiResult<List<ProductsEntity>>> = productsRemoteDataSource.getProducts(id)
    override suspend fun fetchProductById(id: String): Flow<ApiResult<ProductInfoEntity?>> = productsRemoteDataSource.getProductById(id)
    override suspend fun fetchAllProducts(): Flow<ApiResult<List<ProductSearchEntity>>> = productsRemoteDataSource.getAllProducts()
    override suspend fun fetchHomeProducts(sortKeys: ProductSortKeys, reverse: Boolean): Flow<ApiResult<List<ProductsEntity>>> = productsRemoteDataSource.getHomeProducts(sortKeys, reverse)
    override suspend fun insertProductToFavorites(product: ProductSearchEntity) = productsRemoteDataSource.insertProductToFavorites(product)
    override suspend fun getFavoriteProducts(): Flow<ApiResult<List<ProductSearchEntity>>> = productsRemoteDataSource.getFavoriteProducts()
    override suspend fun deleteFavoriteProduct(id: String) = productsRemoteDataSource.deleteFavoriteProduct(id)
}