package com.example.mcommerce.data.remote.auth.shopify

import android.util.Log
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.utils.executeAPI
import com.example.mcommerce.domain.ApiResult
import com.example.mcommerce.domain.entities.CustomerEntity
import kotlinx.coroutines.flow.Flow

class CustomerRemoteDataSourceImp(private val graphQLService: GraphQLService) : CustomerRemoteDataSource {
    override suspend fun createCustomer(customer: CustomerEntity): Flow<ApiResult<String>> =
        executeAPI {
            val editedCustomer = customer.copy(
                phone = "+2${customer.phone}"
            )
            val response = graphQLService.createCustomer(editedCustomer)
            val customerData = response.data?.customerCreate?.customer
            if (!response.hasErrors() && customerData != null) {
                var token: String? = null
                getAccessToken(editedCustomer).collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            Log.i("TAG", "createCustomer: $result")
                        }
                        is ApiResult.Success -> token = result.data
                        is ApiResult.Failure -> throw result.error
                    }
                }
                token ?: throw Exception("Access token was not retrieved")
            } else {
                Log.i("TAG", "createCustomer: ${response.errors?.get(0)?.message}")
                throw Exception("Failed to create the customer on Shopify")
            }
        }

    override suspend fun getAccessToken(customer: CustomerEntity): Flow<ApiResult<String>> =
        executeAPI {
            graphQLService.createCustomerAccessToken(customer).data?.customerAccessTokenCreate?.customerAccessToken?.accessToken ?: ""
        }

}