package com.example.mcommerce.data.remote

import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.GetBrandsQuery

interface GraphQLService {
    suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data>
}