package com.example.mcommerce.data.remote.brands

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CollectionsEntity
import kotlinx.coroutines.flow.Flow

class BrandsRemoteDataSourceImpl(private val graphQlService: GraphQLService): BrandsRemoteDataSource {
    override suspend fun getBrands(): Flow<ApiResult<List<CollectionsEntity>>> =
        executeAPI {
            graphQlService.getBrands().data?.toModel()?.map {
                it.toEntity()
            } ?: listOf()
        }
}