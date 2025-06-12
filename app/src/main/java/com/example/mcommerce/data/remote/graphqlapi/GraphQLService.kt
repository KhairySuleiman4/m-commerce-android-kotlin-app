package com.example.mcommerce.data.remote.graphqlapi

import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.GetCategoriesQuery
import com.example.mcommerce.GetProductsByBrandQuery

interface GraphQLService {
    suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data>
    suspend fun getProducts(id: String): ApolloResponse<GetProductsByBrandQuery.Data>
    suspend fun getCategories(): ApolloResponse<GetCategoriesQuery.Data>
}