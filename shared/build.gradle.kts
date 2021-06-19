import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.apollographql.apollo")
    kotlin("plugin.serialization")
}

version = "1.0"

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {}

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        frameworkName = "shared"
        podfile = project.file("../iosApp/Podfile")
    }

    sourceSets {
        all {
            languageSettings {
                useExperimentalAnnotation("com.apollographql.apollo.api.ApolloExperimental")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsApi")
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsImplementation")
            }
        }
    }
    sourceSets {
        val commonMain by getting{
            dependencies{
                implementation(Lib.KotlinX.Coroutines.bom)
                implementation(Lib.KotlinX.Coroutines.core)
//                {
//                    version {
//                        strictly(Version.coroutines)
//                    }
//                }
                implementation(Lib.Apollo.runtimeKotlin)
                implementation(Lib.KotlinX.dateTime)
                api(Lib.kermit)
                implementation(Lib.stately)
                implementation(Lib.uuid)
                api(Lib.Koin.core)
                implementation(Lib.MultiplatformSettings.settings)
                implementation(Lib.MultiplatformSettings.coroutines)
                implementation(Lib.MultiplatformSettings.test)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies{
                implementation(Lib.MultiplatformSettings.datastore)
                implementation("androidx.datastore:datastore-preferences:1.0.0-beta02")
                implementation("androidx.security:security-crypto:1.1.0-alpha03")
                implementation("androidx.biometric:biometric:1.2.0-alpha03")
                implementation("androidx.work:work-runtime-ktx:2.6.0-beta01")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting
        val iosTest by getting
    }

    targets.withType<KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            isStatic = true
            export(Lib.kermit)
            transitiveExport = true
        }
    }
}

android {
    compileSdk = 30

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 29
        targetSdk = 30
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "11"

    }
}

configure<com.apollographql.apollo.gradle.api.ApolloExtension> {
    generateKotlinModels.set(true)
    customTypeMapping.set(
        mapOf(
            "timestamptz" to "kotlinx.datetime.Instant",
            "uuid" to "com.benasher44.uuid.Uuid",
            "date" to "kotlinx.datetime.LocalDate",
            "smallint" to "kotlin.Int"
        )
    )
}
