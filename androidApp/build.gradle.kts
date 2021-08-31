import util.getLocalProperty

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
    id("com.google.gms.google-services")
    id("io.sentry.android.gradle") version "2.1.4"
}

val hasReleaseKey: Boolean = project.rootProject.file("release/yaba-release.jks").exists()

dependencies {

    implementation(projects.data)
    implementation("androidx.core:core-ktx:1.7.0-alpha01")
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
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha03")
    implementation("androidx.activity:activity-compose:1.3.1")
    //2.4.0-alpha05 uses crossfade by default and breaks the app
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha04")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha01")
    implementation("com.plaid.link:sdk-core:3.5.1")
    implementation(Lib.Accompanist.coil)
    implementation(Lib.Accompanist.insets)
    implementation(Lib.Accompanist.insetsUi)
    implementation(Lib.Accompanist.systemUiController)
    implementation(Lib.uuid)
    implementation(platform(Lib.Firebase.bom))
    implementation(Lib.Firebase.analytics)
    implementation(Lib.Firebase.cloudMessaging)
    implementation(Lib.Firebase.messagingDirectBoot)
    implementation(Lib.AndroidX.work)
    implementation(Lib.AndroidX.workMultiProcess)
    implementation(Lib.Koin.work)
    implementation(platform("io.sentry:sentry-bom:5.1.2"))
    implementation("io.sentry:sentry-android")
    coreLibraryDesugaring(Lib.desugar)
    androidTestImplementation(Lib.AndroidXTest.core)
    androidTestImplementation(Lib.AndroidXTest.rules)
    androidTestImplementation(Lib.AndroidXTest.runner)
    androidTestImplementation(Lib.Compose.uiTest)
    androidTestImplementation(Lib.Compose.uiTestJunit)
    debugImplementation(Lib.Compose.uiTestManifest)
    testImplementation(Lib.Koin.test)
    testImplementation(Lib.Koin.testJunit)
    testImplementation(Lib.junit)
    testImplementation(Lib.AndroidXTest.core)
    testImplementation(Lib.AndroidXTest.robolectric)
    testImplementation(Lib.AndroidXTest.mockito)
    testImplementation(Lib.KotlinX.Coroutines.test)

    val paging_version = "3.0.1"

    implementation("androidx.paging:paging-runtime:$paging_version")
    implementation("androidx.paging:paging-compose:1.0.0-alpha12")
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
            manifestPlaceholders["serverUrl"] = true
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
        freeCompilerArgs = freeCompilerArgs + listOf(
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
            "-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi",
            "-Xuse-experimental=org.koin.core.KoinExperimentalAPI",
            "-Xuse-experimental=coil.annotation.ExperimentalCoilApi"
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
    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

sentry {
    val upload = System.getenv("AUTO_UPLOAD")?.toBoolean() ?: false
    upload.let {
        // Enables or disables the automatic upload of mapping files
        // during a build.  If you disable this, you'll need to manually
        // upload the mapping files with sentry-cli when you do a release.
        autoUpload.set(true)

        // Disables or enables the automatic configuration of Native Symbols
        // for Sentry. This executes sentry-cli automatically so
        // you don't need to do it manually.
        // Default is disabled.
        uploadNativeSymbols.set(true)

        // Does or doesn't include the source code of native code for Sentry.
        // This executes sentry-cli with the --include-sources param. automatically so
        // you don't need to do it manually.
        // Default is disabled.
        includeNativeSources.set(true)
    }
}
