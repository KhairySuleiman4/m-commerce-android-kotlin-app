package com.example.mcommerce.data.remote.admingaraphgl

import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.GetDiscountCodeQuery

interface AdminGraphQLService {
    suspend fun getDiscountCodes(): ApolloResponse<GetDiscountCodeQuery.Data>
}