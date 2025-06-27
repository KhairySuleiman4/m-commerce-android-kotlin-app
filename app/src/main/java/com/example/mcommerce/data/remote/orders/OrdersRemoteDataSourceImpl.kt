package com.example.mcommerce.data.remote.orders

import com.example.mcommerce.data.mappers.toEntity
import com.example.mcommerce.data.mappers.toModel
import com.example.mcommerce.data.remote.admingaraphgl.AdminGraphQLService
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.AddressEntity
import com.example.mcommerce.domain.entities.LineEntity
import com.example.mcommerce.domain.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

class OrdersRemoteDataSourceImpl(
    private val graphQLService: GraphQLService,
    private val adminGraphQLService: AdminGraphQLService
) :OrdersRemoteDataSource {
    override fun createOrder(
        email: String,
        items: List<LineEntity>,
        address: AddressEntity,
        code: String
    ): Flow<ApiResult<Boolean>> = executeAPI {
        val result = adminGraphQLService.createOrder(
            email = email,
            items = items.map { it.toModel() },
            address = address,
            code = code
        ).data
        result?.orderCreate?.order?.name != null
    }

    override fun getOrders(accessToken: String): Flow<ApiResult<List<OrderEntity>>> = executeAPI {
        graphQLService.getOrders(accessToken).data?.toModel()?.map {
            it.toEntity()
        } ?: listOf()
    }
}