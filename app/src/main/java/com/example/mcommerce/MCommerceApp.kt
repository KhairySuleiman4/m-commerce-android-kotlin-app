package com.example.mcommerce

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MCommerceApp: Application(){
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.MAP_API_KEY)
        }
    }
}