plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.eventmanagement"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.eventmanagement"
        minSdk = 24
        targetSdk = 36
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
        viewBinding = true
    }
}

dependencies {
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//    implementation("io.github.jan-tennert.supabase:postgrest-kt:1.4.1")
//    implementation("io.github.jan-tennert.supabase:supabase-kt:1.4.1")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("com.google.android.material:material:1.11.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
//    implementation("io.ktor:ktor-client-android:2.3.8")
//    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
//    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
//    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.4.1")

    implementation("io.github.jan-tennert.supabase:supabase-kt:1.4.1")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:1.4.1")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.4.1")

    implementation("io.ktor:ktor-client-android:2.2.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.4")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}