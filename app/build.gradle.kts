plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.sako"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sako"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ==================== AndroidX Core ====================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ==================== Jetpack Compose ====================
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Compose Material Icons Extended (untuk icon tambahan)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Compose Runtime (untuk collectAsState)
    implementation("androidx.compose.runtime:runtime:1.6.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")

    // ==================== Lifecycle & ViewModel ====================
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    // ==================== Coroutines ====================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ==================== Networking (Retrofit + OkHttp) ====================
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // ==================== DataStore (Preferences) ====================
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-core:1.1.1")

    // ==================== Image Loading ====================
    // Coil for Compose (load images from URL)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.6.0")

    // ==================== Permissions (Runtime Permissions) ====================
    // Accompanist Permissions (untuk handle Camera, Location, Microphone permissions)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // ==================== Location Services ====================
    // Google Play Services Location (untuk GPS/Location)
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // ==================== Google Maps (untuk MapScreen) ====================
    // Google Maps Compose
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // ==================== Camera ====================
    // CameraX for camera capture
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")

    // ==================== QR Code Scanner ====================
    // ML Kit Barcode Scanning (untuk scan QR code)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // Atau alternatif: ZXing (jika tidak mau pakai ML Kit)
    // implementation("com.google.zxing:core:3.5.3")
    // implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // ==================== ExoPlayer (Video Player untuk YouTube) ====================
    // ExoPlayer for video playback
    val exoplayerVersion = "2.19.1"
    implementation("com.google.android.exoplayer:exoplayer:$exoplayerVersion")
    implementation("com.google.android.exoplayer:exoplayer-ui:$exoplayerVersion")
    implementation("com.google.android.exoplayer:exoplayer-core:$exoplayerVersion")

    // ==================== Speech Recognition ====================
    // Android built-in speech recognition (tidak perlu dependency tambahan)
    // Sudah include di Android SDK

    // ==================== Splash Screen ====================
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ==================== System UI Controller ====================
    // Accompanist System UI Controller (untuk control status bar, navigation bar)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    // ==================== Paging (Optional - untuk pagination video/locations) ====================
    // Uncomment jika butuh pagination
    // implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    // implementation("androidx.paging:paging-compose:3.2.1")

    // ==================== Lottie (Optional - untuk animasi) ====================
    // Uncomment jika mau pakai Lottie animations
    // implementation("com.airbnb.android:lottie-compose:6.3.0")

    // ==================== Timber (Optional - untuk logging) ====================
    // Uncomment jika mau pakai Timber untuk logging
    // implementation("com.jakewharton.timber:timber:5.0.1")

    // ==================== Work Manager (Optional - untuk background tasks) ====================
    // Uncomment jika butuh background sync
    // implementation("androidx.work:work-runtime-ktx:2.9.0")

    // ==================== Testing ====================
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.google.truth:truth:1.4.2")
    testImplementation("io.mockk:mockk:1.13.10")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.7")

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}