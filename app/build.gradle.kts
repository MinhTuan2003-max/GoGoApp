plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.gogo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gogo"
        minSdk = 35
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.1.1")

    // Glide cho ảnh (không dùng kapt)
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Unit test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

