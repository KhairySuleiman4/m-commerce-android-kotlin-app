package com.example.mcommerce.data.remote

import com.apollographql.apollo.ApolloClient
import com.example.mcommerce.BuildConfig
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.remote.graphqlapi.GraphQLServiceImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApisManager {

    @Provides
    @Singleton
    fun provideApolloClient() = ApolloClient.Builder()
            .serverUrl(BuildConfig.STORE_URL)
            .addHttpHeader("X-Shopify-Access-Token", BuildConfig.STORE_ACCESS_TOKEN)
            .addHttpHeader("Content-Type", "application/json")
            .build()

    @Provides
    @Singleton
    fun getGraphQLService(apolloClient: ApolloClient): GraphQLService {
        return GraphQLServiceImp(apolloClient)
    }
}