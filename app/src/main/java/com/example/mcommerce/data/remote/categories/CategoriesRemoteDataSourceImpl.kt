package com.example.mcommerce.data.remote.categories

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CategoriesEntity
import kotlinx.coroutines.flow.Flow

class CategoriesRemoteDataSourceImpl(private val graphQLService: GraphQLService) :
    CategoriesRemoteDataSource {
    override suspend fun getCategories(): Flow<ApiResult<List<CategoriesEntity>>> =
        executeAPI {
            graphQLService.getCategories().data?.toModel()?.map {
                it.toEntity()
            } ?: listOf()
        }
}