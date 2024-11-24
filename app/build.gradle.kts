import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "1.9.21-1.0.16"
    kotlin("plugin.serialization") version "1.9.21"
    kotlin("kapt")
}

android {
    namespace = "com.mihan.movie.library"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mihan.movie.library"
        minSdk = 23
        targetSdk = 35
        versionCode = 25
        versionName = "3.0.4"
        vectorDrawables {
            useSupportLibrary = true
        }
        setProperty("archivesBaseName", rootProject.name.lowercase(Locale.getDefault()))
        buildConfigField("String", "ACCESS_TOKEN", gradleLocalProperties(rootDir).getProperty("accessToken"))
        buildConfigField("String", "APP_METRICA_KEY", gradleLocalProperties(rootDir).getProperty("appMetricaKey"))
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
        }
        release {
            versionNameSuffix = "-release"
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    val hiltVersion = "2.51.1"
    val navigationVersion = "1.9.55"
    val retrofitVersion = "2.9.0"
    val coilVersion = "2.5.0"
    val dataStoreVersion = "1.1.1"
    val permissionsVersion = "0.25.1"
    val composeBomVersion = "2024.11.00"
    val roomVersion = "2.6.1"

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.tv:tv-foundation:1.0.0-alpha11")
    implementation("androidx.tv:tv-material:1.0.0")
    implementation("androidx.media3:media3-extractor:1.4.1")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    //Analytics
    implementation("io.appmetrica.analytics:analytics:7.0.0")

    //DI
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt ("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //Navigation
    implementation("io.github.raamcosta.compose-destinations:animations-core:$navigationVersion")
    ksp("io.github.raamcosta.compose-destinations:ksp:$navigationVersion")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")

    //Jsoup
    implementation ("org.jsoup:jsoup:1.17.2")

    //Load Images
    implementation("io.coil-kt:coil-compose:$coilVersion")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:$dataStoreVersion")

    //Checking permissions
    implementation ("com.google.accompanist:accompanist-permissions:$permissionsVersion")

    //Room
    implementation ("androidx.room:room-runtime:$roomVersion")
    implementation ("androidx.room:room-ktx:$roomVersion")
    ksp ("androidx.room:room-compiler:$roomVersion")

    //Tests
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}