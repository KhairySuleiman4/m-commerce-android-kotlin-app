package com.example.mcommerce.data.remote


import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.example.mcommerce.BuildConfig
import com.example.mcommerce.data.remote.exchangerateapi.ExchangeService
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.remote.graphqlapi.GraphQLServiceImp
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApisManager {

    @Provides
    @Singleton
    fun provideGsonConverter() : GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(
        factory: GsonConverterFactory,
    ): Retrofit = Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/"+BuildConfig.EXCHANGE_RATE_API_KEY+"/")
            .addConverterFactory(factory)
            .build()


    @Provides
    @Singleton
    fun getExchangeRateService(
        retrofit: Retrofit): ExchangeService = retrofit.create(ExchangeService::class.java)

    @Provides
    @Singleton
    fun provideApolloClient() = ApolloClient.Builder()
            .serverUrl(BuildConfig.STORE_URL)
            .addHttpHeader("X-Shopify-Storefront-Access-Token", BuildConfig.STORE_ACCESS_TOKEN)
            .addHttpHeader("Content-Type", "application/json")
            .build()

    @Provides
    @Singleton
    fun getGraphQLService(
        apolloClient: ApolloClient): GraphQLService = GraphQLServiceImp(apolloClient)

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient = Places.createClient(context)

}