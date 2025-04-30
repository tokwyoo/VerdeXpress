plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.donations"
    compileSdk = 35

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Compose dependencies (forzadas solo en este módulo)
    implementation("androidx.compose.ui:ui:1.7.0") {
        // Forzar la versión
        exclude(group = "androidx.compose.material3", module = "material3")
    }
    implementation("androidx.compose.material:material:1.7.0") {
        // Forzar la versión
        exclude(group = "androidx.compose.material3", module = "material3")
    }
    implementation("androidx.compose.material3:material3:1.2.1") {
        // Forzar la versión
        exclude(group = "androidx.compose.material", module = "material")
    }
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0") {
        // Forzar la versión
    }
    implementation("androidx.activity:activity-compose:1.8.0") {
        // Forzar la versión
    }
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2") {
        // Forzar la versión
    }

    // Stripe Android SDK (forzada solo en este módulo)
    implementation("com.stripe:stripe-android:21.5.1") {
        // Forzar la versión
    }

    // Kotlin Coroutines (forzada solo en este módulo)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") {
        // Forzar la versión
    }

    // Otras dependencias (sin forzar, a menos que sea necesario)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) {
        exclude(group = "androidx.compose.material3", module = "material3")
    }
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // Firebase
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)

    // CameraX
    implementation(libs.androidx.camera.core)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Módulos locales
    implementation(project(":core:design"))
    implementation(project(":feature:parks"))

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.6.0")
}

// Forzar versiones específicas de las dependencias
configurations.all {
    resolutionStrategy {
        force(
            "androidx.compose.ui:ui:1.7.0",
            "androidx.compose.material:material:1.7.0",
            "androidx.compose.material3:material3:1.2.1",
            "androidx.compose.ui:ui-tooling-preview:1.7.0",
            "androidx.activity:activity-compose:1.8.0",
            "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2",
            "com.stripe:stripe-android:21.5.1",
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
        )
    }
}