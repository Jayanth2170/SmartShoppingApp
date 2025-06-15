plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.smartshoppingcart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartshoppingcart"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ML Kit dependencies
    implementation("com.google.mlkit:object-detection:17.0.2")
    implementation("com.google.mlkit:vision-common:17.0.2")

    // CameraX dependencies
    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")

    // Jetpack Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))

    // Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.animation:animation")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.1")

    // Accompanist permissions (to handle permissions)
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // Guava (for handling collections, concurrency, etc.)
    implementation("com.google.guava:guava:30.1-android")

    // Kotlin dependencies (ensure these versions match)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")

    // Lifecycle dependencies
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Navigation Compose (if you are using navigation in Compose)
    implementation("androidx.navigation:navigation-compose:2.6.0")

    // Coroutines (for managing background tasks and concurrency)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.16.1")








    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")


    // Debug dependencies (Compose tooling for debugging)
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Optional: Force consistent Compose versions (to avoid version conflicts)
    configurations.all {
        resolutionStrategy {
            force("androidx.compose.ui:ui:1.6.8")
        }
    }
}
