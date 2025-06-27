package com.example.mcommerce.data.remote.admingaraphgl

import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.GetDiscountCodeQuery
import com.example.mcommerce.OrderCreateMutation
import com.example.mcommerce.data.models.LineModel
import com.example.mcommerce.domain.entities.AddressEntity

interface AdminGraphQLService {
    suspend fun getDiscountCodes(): ApolloResponse<GetDiscountCodeQuery.Data>
    suspend fun createOrder(email: String, items: List<LineModel>, address: AddressEntity, code: String): ApolloResponse<OrderCreateMutation.Data>

}