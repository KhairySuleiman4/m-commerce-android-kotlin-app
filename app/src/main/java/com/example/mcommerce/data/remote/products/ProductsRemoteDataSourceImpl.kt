package com.example.mcommerce.data.remote.products

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.ProductInfoEntity
import com.example.mcommerce.domain.entities.ProductsEntity
import kotlinx.coroutines.flow.Flow

class ProductsRemoteDataSourceImpl(private val graphQlService: GraphQLService): ProductsRemoteDataSource {
    override suspend fun getProducts(id: String): Flow<ApiResult<List<ProductsEntity>>> =
        executeAPI {
            graphQlService.getProducts(id).data?.toModel()?.map {
                it.toEntity()
            } ?: listOf()
        }

    override suspend fun getProductById(id: String): Flow<ApiResult<ProductInfoEntity>> =
        executeAPI {
            graphQlService.getProductById(id).data?.toModel()!!
        }
}