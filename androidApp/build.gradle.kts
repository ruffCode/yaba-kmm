import util.getLocalProperty

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
    id("com.google.gms.google-services")
    id("io.sentry.android.gradle") version "2.0.1"
}

val hasReleaseKey: Boolean = project.rootProject.file("release/yaba-release.jks").exists()

dependencies {
    implementation(project(":shared"))
    implementation("androidx.core:core-ktx:1.7.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.4.0-alpha03")
    implementation("com.google.android.material:material:1.4.0")
    implementation(Lib.Compose.animation)
    implementation(Lib.Compose.foundation)
    implementation(Lib.Compose.layout)
    implementation(Lib.Compose.iconsExtended)
    implementation(Lib.Compose.material)
    implementation(Lib.Compose.runtime)
    implementation(Lib.Compose.tooling)
    implementation(Lib.Compose.ui)
    implementation(Lib.Compose.preview)

    implementation(platform(Lib.KotlinX.Coroutines.bom))
    implementation(Lib.KotlinX.Coroutines.core)
    implementation(Lib.KotlinX.Coroutines.android)
    implementation(Lib.Koin.core)
    implementation(Lib.Koin.android)
    implementation(Lib.Koin.compose)
    implementation(Lib.kermit)
    implementation(Lib.KotlinX.dateTime)
    implementation(Lib.KotlinX.Serialization.json)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha02")
    implementation("androidx.activity:activity-compose:1.3.0")
    //2.4.0-alpha05 uses crossfade by default and breaks the app
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha04")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-beta01")
    implementation("com.plaid.link:sdk-core:3.4.1")
    implementation(Lib.Accompanist.coil)
    implementation(Lib.Accompanist.insets)
    implementation(Lib.Accompanist.insetsUi)
    implementation(Lib.uuid)
    implementation(platform(Lib.Firebase.bom))
    implementation(Lib.Firebase.analytics)
    implementation(Lib.Firebase.cloudMessaging)
    implementation(Lib.Firebase.messagingDirectBoot)
    implementation(Lib.Jetpack.work)
    implementation(Lib.Jetpack.workMultiProcess)
    implementation("io.sentry:sentry-android:5.1.0-beta.8")
    coreLibraryDesugaring(Lib.desugar)
}

android {
    compileSdk = YabaAndroidConfig.compileSdk
    buildToolsVersion = "31.0.0"
    defaultConfig {
        applicationId = "tech.alexib.yaba"
        minSdk = YabaAndroidConfig.minSdk
        targetSdk = YabaAndroidConfig.targetSdk
        versionCode = YabaAndroidConfig.versionCode
        versionName = YabaAndroidConfig.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["serverUrl"] = "\"https://yabasandbox.alexib.dev/graphql\""
    }

    signingConfigs {
        register("release")
        getByName("debug") {
            storeFile = rootProject.file("release/debug.jks")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    if (hasReleaseKey) {
        signingConfigs["release"].apply {
            keyAlias(getLocalProperty("yaba.key.alias"))
            keyPassword(getLocalProperty("yaba.key.password"))
            storePassword(getLocalProperty("yaba.store.password"))
            storeFile(rootProject.file("release/yaba-release.jks"))
        }
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig =
                if (hasReleaseKey) signingConfigs.getByName("release") else
                    signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            matchingFallbacks += "release"
            signingConfig = signingConfigs.getByName("debug")
        }
        if (project.hasProperty("localServerUrl")) {
            create("staging") {
                initWith(getByName("debug"))
                manifestPlaceholders["serverUrl"] = project.property("localServerUrl") as String
            }
        }
    }
    flavorDimensions.add("environment")

    productFlavors {
        // create("dev") {
        //     dimension = "environment"
        //     applicationIdSuffix = ".dev"
        //     versionNameSuffix = "-dev"
        // }
        create("sandbox") {
            isDefault = true
            dimension = "environment"
            applicationIdSuffix = ".sandbox"
            versionNameSuffix = "-sandbox"
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
            "-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xuse-experimental=kotlin.time.ExperimentalTime",
            "-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi"
        )
    }
    packagingOptions {
        resources {
            excludes.add("META-INF/*")
        }
    }
    lint {
        lintConfig = rootProject.file(".lint/config.xml")
        checkAllWarnings = true
        warningsAsErrors = true
        abortOnError = false
    }
    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

sentry {
// Enables or disables the automatic upload of mapping files
    // during a build.  If you disable this, you'll need to manually
    // upload the mapping files with sentry-cli when you do a release.
    autoUpload.set(false)

    // Disables or enables the automatic configuration of Native Symbols
    // for Sentry. This executes sentry-cli automatically so
    // you don't need to do it manually.
    // Default is disabled.
    uploadNativeSymbols.set(false)

    // Does or doesn't include the source code of native code for Sentry.
    // This executes sentry-cli with the --include-sources param. automatically so
    // you don't need to do it manually.
    // Default is disabled.
    includeNativeSources.set(false)
}
