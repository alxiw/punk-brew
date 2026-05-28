plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.github.alxiw.punkbrew.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 23

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // di
    implementation(libs.koin.android)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // paging
    implementation(libs.androidx.paging.rxjava2.ktx)
    api(libs.androidx.paging.runtime.ktx)

    // db
    implementation(libs.androidx.room.rxjava2)
    implementation(libs.arch.rxjava)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // net
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.adapter.rxjava2)
    implementation(libs.picasso)

    // rx
    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    // util
    implementation(libs.threetenbp)

    // log
    implementation(libs.logging.interceptor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
