plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.core:core-ktx:1.6.0-rc01")
    implementation("androidx.appcompat:appcompat:1.4.0-alpha02")
    implementation("com.google.android.material:material:1.4.0-rc01")
    implementation(Lib.Compose.animation)
    implementation(Lib.Compose.foundation)
    implementation(Lib.Compose.layout)
    implementation(Lib.Compose.iconsExtended)
    implementation(Lib.Compose.material)
    implementation(Lib.Compose.runtime)
    implementation(Lib.Compose.tooling)
    implementation(Lib.Compose.ui)

    implementation(platform(Lib.KotlinX.Coroutines.bom))
    implementation(Lib.KotlinX.Coroutines.core)
    implementation(Lib.KotlinX.Coroutines.android)
    implementation(Lib.Koin.core)
    implementation(Lib.Koin.android)
    implementation(Lib.Koin.compose)
    implementation(Lib.kermit)
    implementation(Lib.KotlinX.dateTime)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha02")
    implementation("androidx.activity:activity-compose:1.3.0-beta02")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha03")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.8")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha08")
    implementation("com.plaid.link:sdk-core:3.4.0")
    implementation(Lib.Accompanist.coil)
    implementation(Lib.Accompanist.insets)
    implementation(Lib.uuid)
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"
    defaultConfig {
        applicationId = "tech.alexib.yaba.kmm.android"
        minSdk = 29
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            buildConfigField("String","APOLLO_URL","\"https://yabasandbox.alexib.dev/graphql\"")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            matchingFallbacks += "release"
//            buildConfigField("String","APOLLO_URL","\"https://ruffrevival.ngrok.io/graphql\"")
        }

    }
    flavorDimensions("environment")
    productFlavors{
        create("prod"){
            dimension = "environment"
            buildConfigField("String","APOLLO_URL","\"https://yabasandbox.alexib.dev/graphql\"")
        }
        create("dev"){
            dimension = "environment"
            buildConfigField("String","APOLLO_URL","\"https://ruffrevival.ngrok.io/graphql\"")
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Version.compose

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xskip-prerelease-check",
            "-Xuse-experimental=kotlin.Experimental",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
            "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
            "-Xuse-experimental=androidx.compose.foundation.ExperimentalFoundationApi",
            "-Xuse-experimental=androidx.compose.material.ExperimentalMaterialApi",
            "-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi"
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "11"
    }
}