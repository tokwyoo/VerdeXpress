plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.verdexpress"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.verdexpress"
        minSdk = 31
        targetSdk = 35
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.osmdroid.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(project(":core:navigation"))
    implementation(libs.firebase.common.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.google.firebase.core)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.activity.compose)
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))

    constraints {
        implementation("com.google.android.gms:play-services-measurement-api:21.5.0")
        implementation("com.google.android.gms:play-services-measurement-impl:21.5.0")
        implementation("com.google.android.gms:play-services-measurement:21.5.0")
    }

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.stripe:stripe-android:20.35.0")
}

configurations.all {
    resolutionStrategy {
        force("com.google.android.gms:play-services-measurement-api:21.5.0")
        force("com.google.android.gms:play-services-measurement-impl:21.5.0")
        force("com.google.android.gms:play-services-measurement:21.5.0")

        // Forzar versiones de Kotlin
        force("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
    }
}

configurations.all {
    resolutionStrategy {
        force("com.google.android.gms:play-services-measurement-api:21.5.0")
        force("com.google.android.gms:play-services-measurement-impl:21.5.0")
        force("com.google.android.gms:play-services-measurement:21.5.0")

        // Forzar todas las dependencias de Kotlin a usar la misma versión
        force("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.24")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
    }
}