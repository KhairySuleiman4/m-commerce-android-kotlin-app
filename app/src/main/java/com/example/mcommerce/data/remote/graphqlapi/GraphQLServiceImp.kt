package com.example.mcommerce.data.remote.graphqlapi

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.GetBrandsQuery

class GraphQLServiceImp(private val client: ApolloClient): GraphQLService {

    override suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data> = client.query(GetBrandsQuery()).execute()

}