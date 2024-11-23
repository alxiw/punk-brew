plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.github.alxiw.punkbrew"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.alxiw.punkbrew"
        minSdk = 23
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.kotlin.stdlib.jdk8)

    // support
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)

    implementation(libs.androidx.paging.rxjava2.ktx)
    implementation(libs.androidx.paging.runtime.ktx)

    // ui
    implementation(libs.material)
    implementation(libs.groupie)
    implementation(libs.groupie.viewbinding)

    // di
    implementation(libs.koin.android)

    // rx
    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    // net
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.adapter.rxjava2)
    implementation(libs.picasso)


    // db
    implementation(libs.androidx.room.rxjava2)
    implementation(libs.arch.rxjava)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // util
    implementation(libs.stetho.okhttp3)
    implementation(libs.timber)

    implementation(libs.threetenbp)

    // log
    implementation(libs.logging.interceptor)

    // unit tests
    testImplementation(libs.junit)

    // ui tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
