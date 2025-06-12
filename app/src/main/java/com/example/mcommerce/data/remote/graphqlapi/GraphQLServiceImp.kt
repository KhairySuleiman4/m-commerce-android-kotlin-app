package com.example.mcommerce.data.remote.graphqlapi

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.GetCategoriesQuery
import com.example.mcommerce.GetProductsByBrandQuery

class GraphQLServiceImp(private val client: ApolloClient): GraphQLService {

    override suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data> = client.query(GetBrandsQuery()).execute()
    override suspend fun getProducts(id: String): ApolloResponse<GetProductsByBrandQuery.Data> = client.query(GetProductsByBrandQuery(
        Optional.present(id))).execute()
    override suspend fun getCategories(): ApolloResponse<GetCategoriesQuery.Data> = client.query(GetCategoriesQuery()).execute()

}