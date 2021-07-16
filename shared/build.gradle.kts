import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.apollographql.apollo")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    id("com.squareup.sqldelight")
}

version = "1.0"

kotlin {
    android()

//    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
//        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) {
//            ::iosArm64
//        } else {
//            ::iosX64
//        }

    val isMac = System.getProperty("os.name").startsWith("Mac")
    if (isMac) {
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) {
            iosArm64("ios")
        } else {
            iosX64("ios") {}
        }
    }

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
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("com.apollographql.apollo.api.ApolloExperimental")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsApi")
                useExperimentalAnnotation(
                    "com.russhwolf.settings.ExperimentalSettingsImplementation"
                )
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
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
                implementation(Lib.statelyConcurrency)
                implementation(Lib.uuid)
                api(Lib.Koin.core)
                implementation(Lib.KotlinX.Serialization.core)
                implementation(Lib.KotlinX.Serialization.json)
                implementation(Lib.MultiplatformSettings.settings)
                implementation(Lib.MultiplatformSettings.coroutines)
                implementation(Lib.MultiplatformSettings.test)
                implementation(Lib.SqlDelight.runtime)
                implementation(Lib.SqlDelight.coroutineExtensions)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {

                implementation(kotlin("stdlib", Version.kotlin))
                implementation(Lib.SqlDelight.androidDriver)
                implementation(Lib.KotlinX.Coroutines.android)
                implementation(Lib.MultiplatformSettings.datastore)
                implementation(Lib.Jetpack.dataStore)
                implementation(Lib.Jetpack.crypto)
                implementation(Lib.Jetpack.biometric)
                implementation(Lib.Jetpack.work)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        if (isMac) {
            val iosMain by getting {
                dependencies {
                    implementation(Lib.KotlinX.Coroutines.core) {
                        version {
                            strictly(Version.coroutines)
                        }
                    }
                    implementation(Lib.SqlDelight.iosDriver)
                }
            }
            val iosTest by getting
        }
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
    lint {
        lintConfig = rootProject.file(".lint/config.xml")
        isCheckAllWarnings = true
        isWarningsAsErrors = true
        isAbortOnError = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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

sqldelight {
    database("YabaDb") {
        packageName = "tech.alexib.yaba.data.db"
        schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
        dialect = "sqlite:3.25"
        linkSqlite = true
    }
}
