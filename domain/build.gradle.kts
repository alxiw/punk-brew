plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.github.alxiw.punkbrew.domain"
    compileSdk = 37

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":data"))
    implementation(libs.androidx.core.ktx)

    // di
    implementation(libs.koin.android)

    // paging
    api(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // rx
    implementation(libs.kotlinx.coroutines.core)

    // util
    implementation(libs.google.code.gson)
    implementation(libs.threetenbp)
}
