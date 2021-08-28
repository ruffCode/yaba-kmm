import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("android-lib")
    id("multiplatform-plugin")
//    kotlin("native.cocoapods")
    id("com.apollographql.apollo3")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    id("com.squareup.sqldelight")
    id("static-analysis")
}

version = "1.0"

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsApi")
                useExperimentalAnnotation(
                    "com.russhwolf.settings.ExperimentalSettingsImplementation"
                )
                useExperimentalAnnotation("com.apollographql.apollo.api.ApolloExperimental")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }
//    cocoapods {
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
//        ios.deploymentTarget = "14.1"
//        frameworkName = "shared"
//        podfile = project.file("../iosApp/Podfile")
//    }
    targets.withType<KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            isStatic = true
            export(Lib.kermit)
            transitiveExport = true
        }
    }
}

dependencies {
    commonMainImplementation(Lib.Apollo.runtimeKotlin)
    commonMainImplementation(Lib.Apollo.adapters)
    commonMainImplementation(Lib.KotlinX.Serialization.core)
    commonMainImplementation(Lib.KotlinX.Serialization.json)
    commonMainImplementation(Lib.MultiplatformSettings.settings)
    commonMainImplementation(Lib.MultiplatformSettings.coroutines)
    commonMainImplementation(Lib.MultiplatformSettings.test)
    commonMainImplementation(Lib.SqlDelight.runtime)
    commonMainImplementation(Lib.SqlDelight.coroutineExtensions)

    androidMainImplementation(Lib.SqlDelight.androidDriver)

    androidMainImplementation(Lib.MultiplatformSettings.datastore)
    androidMainImplementation(Lib.Jetpack.dataStore)
    androidMainImplementation(Lib.Jetpack.crypto)
    androidMainImplementation(Lib.Jetpack.biometric)
    androidMainImplementation(Lib.Jetpack.work)

//    "iosMainImplementation"(Lib.SqlDelight.iosDriver)
}

// kotlin {
//    android()
//
// //    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
// //        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) {
// //            ::iosArm64
// //        } else {
// //            ::iosX64
// //        }
//
//    val isMac = System.getProperty("os.name").startsWith("Mac")
//    if (isMac) {
//        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) {
//            iosArm64("ios")
//        } else {
//            iosX64("ios") {}
//        }
//    }
//
//    cocoapods {
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
//        ios.deploymentTarget = "14.1"
//        frameworkName = "shared"
//        podfile = project.file("../iosApp/Podfile")
//    }
//
//    sourceSets {
//        all {
//            languageSettings {
//                useExperimentalAnnotation("kotlin.RequiresOptIn")
//                useExperimentalAnnotation("com.apollographql.apollo.api.ApolloExperimental")
//                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
//                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsApi")
//                useExperimentalAnnotation(
//                    "com.russhwolf.settings.ExperimentalSettingsImplementation"
//                )
//                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
//            }
//        }
//    }
//    sourceSets {
//        val commonMain by getting {
//            dependencies {
//                implementation(Lib.KotlinX.Coroutines.bom)
//                implementation(Lib.KotlinX.Coroutines.core)
// //                {
// //                    version {
// //                        strictly(Version.coroutines)
// //                    }
// //                }
//                implementation(Lib.Apollo.runtimeKotlin)
//                implementation(Lib.KotlinX.dateTime)
//                api(Lib.kermit)
//                implementation(Lib.stately)
//                implementation(Lib.statelyConcurrency)
//                implementation(Lib.uuid)
//                api(Lib.Koin.core)
//                implementation(Lib.KotlinX.Serialization.core)
//                implementation(Lib.KotlinX.Serialization.json)
//                implementation(Lib.MultiplatformSettings.settings)
//                implementation(Lib.MultiplatformSettings.coroutines)
//                implementation(Lib.MultiplatformSettings.test)
//                implementation(Lib.SqlDelight.runtime)
//                implementation(Lib.SqlDelight.coroutineExtensions)
//            }
//        }
//        val commonTest by getting {
//            dependencies {
//                implementation(kotlin("test-common"))
//                implementation(kotlin("test-annotations-common"))
//            }
//        }
//        val androidMain by getting {
//            dependencies {
//
//                implementation(kotlin("stdlib", Version.kotlin))
//                implementation(Lib.SqlDelight.androidDriver)
//                implementation(Lib.KotlinX.Coroutines.android)
//                implementation(Lib.MultiplatformSettings.datastore)
//                implementation(Lib.Jetpack.dataStore)
//                implementation(Lib.Jetpack.crypto)
//                implementation(Lib.Jetpack.biometric)
//                implementation(Lib.Jetpack.work)
//            }
//        }
//        val androidTest by getting {
//            dependencies {
//                implementation(kotlin("test-junit"))
//                implementation("junit:junit:4.13.2")
//            }
//        }
//        if (isMac) {
//            val iosMain by getting {
//                dependencies {
//                    implementation(Lib.KotlinX.Coroutines.core) {
//                        version {
//                            strictly(Version.coroutines)
//                        }
//                    }
//                    implementation(Lib.SqlDelight.iosDriver)
//                }
//            }
//            val iosTest by getting
//        }
//    }
//
//    targets.withType<KotlinNativeTarget> {
//        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
//            isStatic = true
//            export(Lib.kermit)
//            transitiveExport = true
//        }
//    }
// }

// android {
//    compileSdk = YabaAndroidConfig.compileSdk
//
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    defaultConfig {
//        minSdk = YabaAndroidConfig.minSdk
//        targetSdk = YabaAndroidConfig.targetSdk
//    }
//    lint {
//        lintConfig = rootProject.file(".lint/config.xml")
//        checkAllWarnings = true
//        warningsAsErrors = true
//        abortOnError = false
//    }
// }

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

// configure<com.apollographql.apollo.gradle.api.ApolloExtension> {
// //    generateKotlinModels.set(true)
//    customTypeMapping.set(
//        mapOf(
//            "timestamptz" to "kotlinx.datetime.Instant",
//            "uuid" to "com.benasher44.uuid.Uuid",
//            "date" to "kotlinx.datetime.LocalDate",
//            "smallint" to "kotlin.Int"
//        )
//    )
// }

// apollo{
//        customTypeMapping.set(
//        mapOf(
//            "timestamptz" to "kotlinx.datetime.Instant",
//            "uuid" to "com.benasher44.uuid.Uuid",
//            "date" to "kotlinx.datetime.LocalDate",
//            "smallint" to "kotlin.Int"
//        )
//    )
// }

configure<com.apollographql.apollo3.gradle.api.ApolloExtension> {
//    customScalarsMapping.set(
//        mapOf(
//            "timestamptz" to "com.apollographql.apollo3.adapter.InstantAdapter",
//            "uuid" to "com.benasher44.uuid.Uuid",
//            "date" to "com.apollographql.apollo3.adapter.LocalDateAdapter",
//            "smallint" to "kotlin.Int"
//        )
//    )
//    packageName.set("tech.alexib.yaba")
    customScalarsMapping.set(
        mapOf(
//            "timestamptz" to "com.apollographql.apollo3.adapter.InstantAdapter",
            "UUID" to "com.benasher44.uuid.Uuid",
            "LocalDate" to "com.apollographql.apollo3.adapter.LocalDateAdapter",
//            "smallint" to "kotlin.Int"
        )
    )
    packageName.set("tech.alexib.yaba")
}
// apollo{
//    customScalarsMapping.set(
//        mapOf(
// //            "timestamptz" to "com.apollographql.apollo3.adapter.InstantAdapter",
//            "UUID" to "com.benasher44.uuid.Uuid",
//            "LocalDate" to "com.apollographql.apollo3.adapter.LocalDateAdapter",
// //            "smallint" to "kotlin.Int"
//        )
//    )
//    packageName.set("tech.alexib.yaba")
// }
sqldelight {
    database("YabaDb") {
        packageName = "tech.alexib.yaba.data.db"
        schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
        dialect = "sqlite:3.25"
        linkSqlite = true
    }
}
