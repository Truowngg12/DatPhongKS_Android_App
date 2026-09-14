plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.datphongks"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.datphongks"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // Lifecycle components for MVVM
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")

    // Glide for Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Shimmer for Loading Effect
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // ===== Firebase Auth (Google Sign-In) - Phần 1 =====
    // LƯU Ý: để build được với các dòng này, bạn cần:
    //   1) Tạo project trên https://console.firebase.google.com, thêm app Android
    //      với đúng applicationId "com.example.datphongks"
    //   2) Tải file google-services.json, bỏ vào thư mục app/ (ngang hàng build.gradle.kts)
    //   3) Thêm plugin "com.google.gms.google-services" vào build.gradle.kts (cấp root)
    //      và "alias(libs.plugins.google.gms.google.services)" (hoặc id trực tiếp) ở đây
    // Nếu chưa có google-services.json, cứ để các dòng dependency dưới đây, project
    // vẫn BUILD/COMPILE bình thường (chỉ khi chạy code Firebase lúc runtime mới cần file đó).
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}