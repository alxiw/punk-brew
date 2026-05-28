import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.github.alxiw.punkbrew"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.alxiw.punkbrew"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf("room.incremental" to "true"))
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.kotlin.stdlib.jdk8)

    implementation(project(":simplesearchview"))

    // support
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.viewbindingdelegate)

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
    implementation(libs.threetenbp)
    implementation(libs.stetho.okhttp3)

    // log
    implementation(libs.logging.interceptor)

    // unit tests
    testImplementation(libs.junit)

    // ui tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
