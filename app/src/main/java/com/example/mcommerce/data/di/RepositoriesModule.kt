package com.example.mcommerce.data.di

import android.content.Context
import android.location.Geocoder
import com.example.mcommerce.data.remote.auth.firebase.Firebase
import com.example.mcommerce.data.remote.auth.firebase.FirebaseImp
import com.example.mcommerce.data.remote.auth.shopify.CustomerRemoteDataSource
import com.example.mcommerce.data.remote.auth.shopify.CustomerRemoteDataSourceImp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.mcommerce.data.datastore.ExchangeDataStore
import com.example.mcommerce.data.datastore.ExchangeDataStoreImp
import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSource
import com.example.mcommerce.data.remote.brands.BrandsRemoteDataSourceImpl
import com.example.mcommerce.data.remote.categories.CategoriesRemoteDataSource
import com.example.mcommerce.data.remote.categories.CategoriesRemoteDataSourceImpl
import com.example.mcommerce.data.remote.currency.CurrencyRemoteDataSource
import com.example.mcommerce.data.remote.currency.CurrencyRemoteDataSourceImp
import com.example.mcommerce.data.remote.exchangerateapi.ExchangeService
import com.example.mcommerce.data.remote.graphqlapi.GraphQLService
import com.example.mcommerce.data.remote.map.MapRemoteDataSource
import com.example.mcommerce.data.remote.map.MapRemoteDataSourceImp
import com.example.mcommerce.data.remote.products.ProductsRemoteDataSource
import com.example.mcommerce.data.remote.products.ProductsRemoteDataSourceImpl
import com.example.mcommerce.data.repoimp.AuthenticationRepoImp
import com.example.mcommerce.data.repoimp.BrandsRepoImpl
import com.example.mcommerce.data.repoimp.CategoriesRepoImpl
import com.example.mcommerce.data.repoimp.CurrencyRepoImp
import com.example.mcommerce.data.repoimp.MapRepoImp
import com.example.mcommerce.data.repoimp.ProductsRepoImpl
import com.example.mcommerce.domain.repoi.AuthenticationRepo
import com.example.mcommerce.domain.repoi.BrandsRepo
import com.example.mcommerce.domain.repoi.CategoriesRepo
import com.example.mcommerce.domain.repoi.CurrencyRepo
import com.example.mcommerce.domain.repoi.MapRepo
import com.example.mcommerce.domain.repoi.ProductsRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule{
    @Provides
    @Singleton
    fun provideBrandsRepository(brandsRemoteDataSource: BrandsRemoteDataSource): BrandsRepo = BrandsRepoImpl(brandsRemoteDataSource)

    @Provides
    @Singleton
    fun provideProductsRepository(productsRemoteDataSource: ProductsRemoteDataSource): ProductsRepo = ProductsRepoImpl(productsRemoteDataSource)

    @Provides
    @Singleton
    fun provideCategoriesRepository(categoriesRemoteDataSource: CategoriesRemoteDataSource): CategoriesRepo = CategoriesRepoImpl(categoriesRemoteDataSource)

    @Provides
    @Singleton
    fun provideBrandsDataSource(graphQLService: GraphQLService): BrandsRemoteDataSource = BrandsRemoteDataSourceImpl(graphQLService)

    @Provides
    @Singleton
    fun provideProductsDataSource(graphQLService: GraphQLService, firestore: Firebase): ProductsRemoteDataSource = ProductsRemoteDataSourceImpl(graphQLService, firestore)

    @Provides
    @Singleton
    fun provideCategoriesDataSource(graphQLService: GraphQLService): CategoriesRemoteDataSource = CategoriesRemoteDataSourceImpl(graphQLService)

    @Provides
    @Singleton
    fun provideExchangeDataStore(
        preferences: DataStore<Preferences>): ExchangeDataStore = ExchangeDataStoreImp(preferences)

    @Provides
    @Singleton
    fun provideCurrencyRemoteDataSource(
        retrofitService: ExchangeService): CurrencyRemoteDataSource = CurrencyRemoteDataSourceImp(retrofitService)

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        local: ExchangeDataStore,
        remote: CurrencyRemoteDataSource
    ): CurrencyRepo = CurrencyRepoImp(local,remote)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebase(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): Firebase = FirebaseImp(firebaseAuth, firestore)

    @Provides
    @Singleton
    fun provideCustomerRemoteDataSource(graphQLService: GraphQLService): CustomerRemoteDataSource = CustomerRemoteDataSourceImp(graphQLService)

    @Provides
    @Singleton
    fun provideAuthRepository(remote: CustomerRemoteDataSource, firebase: Firebase): AuthenticationRepo = AuthenticationRepoImp(firebase, remote)

    @Provides
    @Singleton
    fun provideGeoCoder(@ApplicationContext context: Context): Geocoder = Geocoder(context,
        Locale("en")
    )

    @Provides
    @Singleton
    fun provideMapRemoteDataSource(geocoder: Geocoder): MapRemoteDataSource = MapRemoteDataSourceImp(geocoder)

    @Provides
    @Singleton
    fun provideMapRepository(mapRemoteDataSource: MapRemoteDataSource): MapRepo = MapRepoImp(mapRemoteDataSource)

}