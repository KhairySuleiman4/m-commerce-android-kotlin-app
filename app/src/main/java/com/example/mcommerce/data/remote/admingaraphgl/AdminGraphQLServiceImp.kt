package com.example.mcommerce.data.remote.admingaraphgl

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.GetDiscountCodeQuery

class AdminGraphQLServiceImp(
    private val apolloClient: ApolloClient
) : AdminGraphQLService {
    override suspend fun getDiscountCodes(): ApolloResponse<GetDiscountCodeQuery.Data> =
        apolloClient.query(GetDiscountCodeQuery()).execute()
}