package com.example.mcommerce.data.di

import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSource
import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSourceImpl
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.repoimp.AppRepoImp
import com.example.mcommerce.domain.repoi.AppRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule{
    @Provides
    @Singleton
    fun provideRepository(remoteDataSource: BrandsRemoteDataSource): AppRepo{
        return AppRepoImp(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideBrandsDataSource(graphQLService: GraphQLService): BrandsRemoteDataSource{
        return BrandsRemoteDataSourceImpl(graphQLService)
    }
}