package com.example.mcommerce.data.remote.graphqlapi

import com.apollographql.apollo.api.ApolloResponse
import com.example.mcommerce.CustomerAccessTokenCreateMutation
import com.example.mcommerce.CustomerCreateMutation
import com.example.mcommerce.GetAllProductsQuery
import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.GetCategoriesQuery
import com.example.mcommerce.GetProductByIdQuery
import com.example.mcommerce.GetProductsByBrandQuery
import com.example.mcommerce.domain.entities.CustomerEntity

interface GraphQLService {
    suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data>
    suspend fun getProducts(id: String): ApolloResponse<GetProductsByBrandQuery.Data>
    suspend fun createCustomer(customer: CustomerEntity): ApolloResponse<CustomerCreateMutation.Data>
    suspend fun createCustomerAccessToken(customer: CustomerEntity): ApolloResponse<CustomerAccessTokenCreateMutation.Data>
    suspend fun getCategories(): ApolloResponse<GetCategoriesQuery.Data>
    suspend fun getProductById(id: String): ApolloResponse<GetProductByIdQuery.Data>
    suspend fun getAllProducts(): ApolloResponse<GetAllProductsQuery.Data>
}