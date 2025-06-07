package com.example.mcommerce.data.remote.graphqlapi

import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.GetBrandsQuery

interface GraphQLService {
    suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data>
}