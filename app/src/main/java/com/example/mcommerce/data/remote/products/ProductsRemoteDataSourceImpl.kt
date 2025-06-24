package com.example.mcommerce.data.remote.products

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.remote.auth.firebase.Firebase
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductSearchEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import com.example.mcommerce.type.ProductSortKeys
import kotlinx.coroutines.flow.Flow

class ProductsRemoteDataSourceImpl(
    private val graphQlService: GraphQLService,
    private val firestore: Firebase
) : ProductsRemoteDataSource {
    override suspend fun getProducts(id: String): Flow<ApiResult<List<ProductsEntity>>> =
        executeAPI {
            graphQlService.getProducts(id).data?.toModel()?.map {
                it.toEntity()
            } ?: listOf()
        }

    override suspend fun getProductById(id: String): Flow<ApiResult<ProductInfoEntity?>> =
        executeAPI {
            graphQlService.getProductById(id).data?.toModel()
        }

    override suspend fun getAllProducts(): Flow<ApiResult<List<ProductSearchEntity>>> =
        executeAPI {
            graphQlService.getAllProducts().data?.toModel()?.map {
                it.toEntity()
            } ?: listOf()
        }

    override suspend fun getHomeProducts(
        sortKeys: ProductSortKeys,
        reverse: Boolean
    ): Flow<ApiResult<List<ProductsEntity>>> =
        executeAPI {
            graphQlService.getHomeProducts(sortKeys, reverse).data?.toModel()?.map {
                it.toEntity()
            } ?: listOf()
        }

    override suspend fun insertProductToFavorites(product: ProductSearchEntity) =
        firestore.insertProductToFavorites(product)

    override suspend fun getFavoriteProducts(): Flow<ApiResult<List<ProductSearchEntity>>> =
        firestore.getFavoriteProducts()

    override suspend fun deleteFavoriteProduct(id: String) = firestore.deleteProduct(id)
}