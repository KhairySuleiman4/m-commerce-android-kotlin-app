package com.example.mcommerce.data.di

import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSource
import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSourceImpl
import com.example.mcommerce.data.remote.categories.CategoriesRemoteDataSource
import com.example.mcommerce.data.remote.categories.CategoriesRemoteDataSourceImpl
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.remote.products.ProductsRemoteDataSource
import com.example.mcommerce.data.remote.products.ProductsRemoteDataSourceImpl
import com.example.mcommerce.data.repoimp.BrandsRepoImpl
import com.example.mcommerce.data.repoimp.CategoriesRepoImpl
import com.example.mcommerce.data.repoimp.ProductsRepoImpl
import com.example.mcommerce.domain.repoi.BrandsRepo
import com.example.mcommerce.domain.repoi.CategoriesRepo
import com.example.mcommerce.domain.repoi.ProductsRepo
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
    fun provideBrandsRepository(brandsRemoteDataSource: BrandsRemoteDataSource): BrandsRepo{
        return BrandsRepoImpl(brandsRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideProductsRepository(productsRemoteDataSource: ProductsRemoteDataSource): ProductsRepo{
        return ProductsRepoImpl(productsRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideCategoriesRepository(categoriesRemoteDataSource: CategoriesRemoteDataSource): CategoriesRepo{
        return CategoriesRepoImpl(categoriesRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideBrandsDataSource(graphQLService: GraphQLService): BrandsRemoteDataSource{
        return BrandsRemoteDataSourceImpl(graphQLService)
    }

    @Provides
    @Singleton
    fun provideProductsDataSource(graphQLService: GraphQLService): ProductsRemoteDataSource{
        return ProductsRemoteDataSourceImpl(graphQLService)
    }

    @Provides
    @Singleton
    fun provideCategoriesDataSource(graphQLService: GraphQLService): CategoriesRemoteDataSource{
        return CategoriesRemoteDataSourceImpl(graphQLService)
    }
}