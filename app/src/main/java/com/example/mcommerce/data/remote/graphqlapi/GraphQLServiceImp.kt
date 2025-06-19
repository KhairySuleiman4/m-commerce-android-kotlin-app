package com.example.mcommerce.data.remote.graphqlapi

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.example.mcommerce.CustomerAccessTokenCreateMutation
import com.example.mcommerce.CustomerCreateMutation
import com.example.mcommerce.GetAllProductsQuery
import com.example.mcommerce.GetBrandsQuery
import com.example.mcommerce.GetCategoriesQuery
import com.example.mcommerce.GetOrdersQuery
import com.example.mcommerce.GetProductByIdQuery
import com.example.mcommerce.GetProductsByBrandQuery
import com.example.mcommerce.domain.entities.CustomerEntity
import com.example.mcommerce.type.CustomerAccessTokenCreateInput
import com.example.mcommerce.type.CustomerCreateInput

class GraphQLServiceImp(private val client: ApolloClient) : GraphQLService {

    override suspend fun getBrands(): ApolloResponse<GetBrandsQuery.Data> = client.query(GetBrandsQuery()).execute()
    override suspend fun getProducts(id: String): ApolloResponse<GetProductsByBrandQuery.Data> = client.query(GetProductsByBrandQuery(Optional.present(id))).execute()
    override suspend fun getCategories(): ApolloResponse<GetCategoriesQuery.Data> = client.query(GetCategoriesQuery()).execute()
    override suspend fun getProductById(id: String): ApolloResponse<GetProductByIdQuery.Data> = client.query(GetProductByIdQuery(Optional.present(id))).execute()

    override suspend fun createCustomer(customer: CustomerEntity): ApolloResponse<CustomerCreateMutation.Data> =
        client.mutation(
            CustomerCreateMutation(
                CustomerCreateInput(
                    firstName = Optional.present(customer.name),
                    phone = Optional.present(customer.phone),
                    password = customer.password,
                    email = customer.email
                )
            )
        ).execute()

    override suspend fun createCustomerAccessToken(customer: CustomerEntity): ApolloResponse<CustomerAccessTokenCreateMutation.Data> =
        client.mutation(
            CustomerAccessTokenCreateMutation(
                CustomerAccessTokenCreateInput(
                    customer.email,
                    customer.password
                )
            )
        ).execute()

    override suspend fun getAllProducts(): ApolloResponse<GetAllProductsQuery.Data> = client.query(GetAllProductsQuery()).execute()

    override suspend fun getOrders(userAccessToken: String): ApolloResponse<GetOrdersQuery.Data> = client.query(GetOrdersQuery(userAccessToken)).execute()
}