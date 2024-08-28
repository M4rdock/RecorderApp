

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.rkoma.recorderapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rkoma.recorderapp"
        minSdk = 24
        targetSdk = 34
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
        jvmTarget = "1.8"
    }
}

dependencies {


    val kotlin_version: String by project
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    //kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //implementation(libs.androidx.ui.test.android)
    //implementation(libs.androidx.ui.test.desktop)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))


    //implementation(libs.androidx.core.ktx.v1101)
    //implementation(libs.androidx.appcompat.v161)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    //androidTestImplementation(libs.androidx.junit.v115)
    //androidTestImplementation(libs.androidx.espresso.core.v351)

    //implementation(libs.material.v1100)

    implementation(libs.androidx.room.runtime)
    //implementation(libs.androidx.room.ktx)

    //ho commentato perchè potrebbe esserci un errore con queste implementazioni
}

