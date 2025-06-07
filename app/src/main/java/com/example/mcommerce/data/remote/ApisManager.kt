package com.example.mcommerce.data.remote

import com.apollographql.apollo.ApolloClient
import com.example.mcommerce.BuildConfig
import com.example.mcommerce.data.remote.exchangerateapi.ApiKeyInterceptor
import com.example.mcommerce.data.remote.exchangerateapi.ExchangeService
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.remote.graphqlapi.GraphQLServiceImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApisManager {

    @Provides
    @Singleton
    fun provideGsonConverter() : GsonConverterFactory{
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideApiKeyInterceptor() : Interceptor{
        return ApiKeyInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiKeyInterceptor: Interceptor,
    ): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        factory: GsonConverterFactory,
        client: OkHttpClient
    ): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/")
            .addConverterFactory(factory)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun getExchangeRateService(
        retrofit: Retrofit
    ): ExchangeService{
        return retrofit.create(ExchangeService::class.java)
    }

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