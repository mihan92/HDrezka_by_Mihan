import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    kotlin("kapt")
}

android {
    namespace = "com.mihan.movie.library"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mihan.movie.library"
        minSdk = 28
        targetSdk = 34
        versionCode = 13
        versionName = "0.7.2"
        vectorDrawables {
            useSupportLibrary = true
        }
        setProperty("archivesBaseName", rootProject.name.lowercase(Locale.getDefault()))
        buildConfigField("String", "ACCESS_TOKEN", gradleLocalProperties(rootDir).getProperty("accessToken"))
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
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

    val hiltVersion = "2.50"
    val navigationVersion = "1.9.55"
    val retrofitVersion = "2.9.0"
    val coilVersion = "2.5.0"
    val dataStoreVersion = "1.0.0"
    val permissionsVersion = "0.25.1"
    val composeBomVersion = "2024.04.00"
    val roomVersion = "2.6.1"
    val exoPlayerVersion = "1.3.1"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.tv:tv-foundation:1.0.0-alpha07") //пока не трогать, много чего экспериментального
    implementation("androidx.tv:tv-material:1.0.0-alpha07") //пока не трогать, много чего экспериментального
    implementation("androidx.media3:media3-extractor:1.3.1")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

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