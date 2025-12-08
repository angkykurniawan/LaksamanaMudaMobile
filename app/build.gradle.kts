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
    // SUPABASE & NETWORKING
    implementation("io.github.jan-tennert.supabase:supabase-kt:1.4.1")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:1.4.1")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.4.1")

    // GANTI SEMUA VERSI KTOR KE VERSI TERBARU YANG SESUAI (misal: 2.3.8)
    implementation("io.ktor:ktor-client-android:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")

    // Gunakan versi JSON terbaru yang kompatibel dengan Ktor 2.3.8
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // Naikkan dari 1.5.1

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Event
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Gambar String
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // DEPENDENCY TAMBAHAN YANG DISARANKAN UNTUK KOMPONEN UI
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // OkHttp (NGROK)
    // OkHttp (Jaringan)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Gson (JSON)
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines (Asinkron)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Untuk UI (jika Anda menggunakan TextInputLayout)
    implementation("com.google.android.material:material:1.11.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}