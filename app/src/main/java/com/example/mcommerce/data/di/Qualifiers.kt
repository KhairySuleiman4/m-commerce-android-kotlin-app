package com.example.mcommerce.data.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StoreClient