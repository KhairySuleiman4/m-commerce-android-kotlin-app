package com.example.mcommerce.data.remote.orders

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

class OrdersRemoteDataSourceImpl(
    private val graphQLService: GraphQLService
) :OrdersRemoteDataSource {
    override fun getOrders(accessToken: String): Flow<ApiResult<List<OrderEntity>>> = executeAPI {
        graphQLService.getOrders(accessToken).data?.toModel()?.map {
            it.toEntity()
        } ?: listOf()
    }
}