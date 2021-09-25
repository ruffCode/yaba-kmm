import util.getLocalProperty

plugins {
    id("base-convention")
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
    id("com.google.gms.google-services")
    id("io.sentry.android.gradle")
}

val hasReleaseKey: Boolean = project.rootProject.file("release/yaba-release.jks").exists()

dependencies {

    implementation(projects.data)
    implementation(Google.Android.material)
    // Compose
    with(AndroidX) {
        implementation(compose.animation)
        implementation(compose.ui.graphics)
        implementation(compose.foundation.layout)
        implementation(compose.material.icons.extended)
        implementation(compose.material)
        implementation(compose.runtime)
        implementation(compose.ui.tooling)
        implementation(compose.ui)
        implementation(compose.ui.toolingPreview)
        debugImplementation(compose.ui.testManifest)
    }
    implementation("io.coil-kt:coil-compose:_")

    with(Lib.KotlinX.Coroutines) {
        implementation(platform(bom))
        implementation(core)
        implementation(android)
        testImplementation(test)
    }

    with(KotlinX) {
        implementation(datetime)
        implementation(serialization.json)
    }

    with(AndroidX) {
        implementation(lifecycle.runtimeKtx)
        implementation(lifecycle.viewModelKtx)
        implementation(lifecycle.viewModelCompose)
        implementation(activity.compose)
        implementation(lifecycle.viewModelSavedState)
        implementation(constraintLayoutCompose)
        implementation(core.splashscreen)
        implementation(work.runtimeKtx)
        implementation(work.multiprocess)
    }
    // Koin
    with(Koin) {
        implementation(android)
        implementation(compose)
        implementation(workManager)
        testImplementation(test)
        testImplementation(junit4)
    }
    implementation(Lib.kermit)

    implementation(Lib.Accompanist.navigationAnimation)

    implementation("com.plaid.link:sdk-core:_")

    with(Google) {
        implementation(accompanist.insets)
        implementation(accompanist.insets.ui)
        implementation(accompanist.systemuicontroller)
    }
    implementation(Lib.uuid)

    with(Lib.Firebase) {
        implementation(platform(bom))
        implementation(analytics)
        implementation(cloudMessaging)
        implementation(messagingDirectBoot)
    }

    implementation(platform("io.sentry:sentry-bom:_"))
    implementation("io.sentry:sentry-android")

    coreLibraryDesugaring(Lib.desugar)

    // Test
    with(AndroidX) {
        androidTestImplementation(test.ext.junitKtx)
        androidTestImplementation(test.rules)
        androidTestImplementation(test.runner)
        androidTestImplementation(compose.ui.test)
        androidTestImplementation(compose.ui.testJunit4)
        testImplementation(test.coreKtx)
    }

    testImplementation(Lib.junit)

    testImplementation(Testing.robolectric)
    testImplementation(Testing.mockito.inline)

    debugImplementation(Square.leakCanary.android)
    implementation(Lib.logCat)
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
            "-Xopt-in=kotlin.Experimental",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-Xopt-in=org.koin.core.KoinExperimentalAPI",
            "-Xopt-in=coil.annotation.ExperimentalCoilApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
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
    // Enables or disables the automatic upload of mapping files
    // during a build.  If you disable this, you'll need to manually
    // upload the mapping files with sentry-cli when you do a release.
    autoUpload.set(upload)
    // Disables or enables the automatic configuration of Native Symbols
    // for Sentry. This executes sentry-cli automatically so
    // you don't need to do it manually.
    // Default is disabled.
    uploadNativeSymbols.set(upload)
    // Does or doesn't include the source code of native code for Sentry.
    // This executes sentry-cli with the --include-sources param. automatically so
    // you don't need to do it manually.
    // Default is disabled.
    includeNativeSources.set(upload)
}
