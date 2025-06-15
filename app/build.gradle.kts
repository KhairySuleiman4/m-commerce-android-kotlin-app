import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.apollographql.apollo") version "4.2.0"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "1.9.23"
    id("com.google.gms.google-services")
}

apollo {
    service("service") {
        packageName.set("com.example.mcommerce")
    }
}

android {
    namespace = "com.example.mcommerce"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mcommerce"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "STORE_URL", "\"${properties.getProperty("STORE_URL")}\"")
        buildConfigField("String", "STORE_ACCESS_TOKEN", "\"${properties.getProperty("STORE_ACCESS_TOKEN")}\"")
        buildConfigField("String", "EXCHANGE_RATE_API_KEY", "\"${properties.getProperty("EXCHANGE_RATE_API_KEY")}\"")
        buildConfigField("String", "MAP_API_KEY", "\"${properties.getProperty("MAP_API_KEY")}\"")
        resValue("string", "google_maps_key","\"${properties.getProperty("MAP_API_KEY")}\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Apollo GraphQL
    implementation(libs.apollographql.apollo.runtime)
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Google Maps
    implementation(libs.maps.compose)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Glide
    implementation(libs.compose)
    // Serializable
    implementation(libs.kotlinx.serialization.json)
    // Constraint Layout
    implementation(libs.androidx.constraintlayout.compose)
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
}

kapt {
    correctErrorTypes = true
}