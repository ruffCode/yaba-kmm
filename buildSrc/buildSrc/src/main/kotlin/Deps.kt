/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:SuppressWarnings()

object Lib {
    const val kermit = "co.touchlab:kermit:0.1.9"
    const val stately = "co.touchlab:stately-common:1.1.10"
    const val statelyConcurrency = "co.touchlab:stately-concurrency:1.1.10"
    const val desugar = "com.android.tools:desugar_jdk_libs:1.1.5"
    const val uuid = "com.benasher44:uuid:0.3.0"
    const val turbine = "app.cash.turbine:turbine:0.6.1"
    const val junit = "junit:junit:4.13.2"
    const val robolectric = "org.robolectric:robolectric:4.6.1"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.7"
    const val logCat = "com.squareup.logcat:logcat:0.1"

    object Koin {
        private const val version = "3.1.2"
        const val core = "io.insert-koin:koin-core:$version"
        const val test = "io.insert-koin:koin-test:$version"
        const val testJunit = "io.insert-koin:koin-test-junit4:$version"
        const val android = "io.insert-koin:koin-android:$version"
        const val compose = "io.insert-koin:koin-androidx-compose:$version"
        const val work = "io.insert-koin:koin-androidx-workmanager:$version"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:28.0.1"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val messagingDirectBoot = "com.google.firebase:firebase-messaging-directboot"
        const val cloudMessaging = "com.google.firebase:firebase-messaging-ktx"
    }

    object Apollo {
        private const val version = Version.apollo
        const val runtimeKotlin = "com.apollographql.apollo3:apollo-runtime:$version"
        const val adapters = "com.apollographql.apollo3:apollo-adapters:$version"
    }

    object MultiplatformSettings {
        private const val version = "0.8"
        const val settings = "com.russhwolf:multiplatform-settings:$version"
        const val coroutines = "com.russhwolf:multiplatform-settings-coroutines:$version"
        const val datastore = "com.russhwolf:multiplatform-settings-datastore:$version"
        const val test = "com.russhwolf:multiplatform-settings-test:$version"
    }

    object AndroidX {

        const val activity = "androidx.activity:activity-compose:1.3.1"
        const val biometric = "androidx.biometric:biometric-ktx:1.2.0-alpha03"
        const val browser = "androidx.browser:browser:1.3.0"
        const val constrainLayout =
            "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02"
        const val crypto = "androidx.security:security-crypto-ktx:1.1.0-alpha03"
        const val dataStore = "androidx.datastore:datastore-preferences:1.0.0"

        //to match navigation-animation
        const val navigation = "androidx.navigation:navigation-compose:2.4.0-alpha07"
        const val startup = "androidx.startup:startup-runtime:1.0.0"
        const val work = "androidx.work:work-runtime-ktx:2.7.0-beta01"
        const val workMultiProcess = "androidx.work:work-multiprocess:2.7.0-beta01"

        object Lifecycle {
            private const val version = "2.4.0-beta01"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
            const val savedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$version"
        }
    }

    object KotlinX {

        object Serialization {
            private const val version = "1.2.2"
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:$version"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
        }

        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:0.2.1"

        object Coroutines {
            private const val version = Version.coroutines
            const val bom = "org.jetbrains.kotlinx:kotlinx-coroutines-bom:$version"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
        }
    }

    object AndroidXTest {
        val core = "androidx.test:core:${Version.AndroidX.test}"
        val junit = "androidx.test.ext:junit:${Version.AndroidX.test_ext}"
        val runner = "androidx.test:runner:${Version.AndroidX.test}"
        val rules = "androidx.test:rules:${Version.AndroidX.test}"
        const val mockito = "org.mockito:mockito-inline:3.11.2"
        const val robolectric = "org.robolectric:robolectric:4.6.1"

    }

    object KotlinTest {
        val common = "org.jetbrains.kotlin:kotlin-test-common:${Version.kotlin}"
        val annotations = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Version.kotlin}"
        val jvm = "org.jetbrains.kotlin:kotlin-test:${Version.kotlin}"
        val junit = "org.jetbrains.kotlin:kotlin-test-junit:${Version.kotlin}"
    }

    object SqlDelight {
        private const val version = Version.sqlDelight
        const val runtime = "com.squareup.sqldelight:runtime:$version"
        const val coroutineExtensions = "com.squareup.sqldelight:coroutines-extensions:$version"
        const val androidDriver = "com.squareup.sqldelight:android-driver:$version"
        const val iosDriver = "com.squareup.sqldelight:native-driver:$version"
        const val gradlePlugin = "com.squareup.sqldelight:gradle-plugin:$version"
        const val jvm = "com.squareup.sqldelight:sqlite-driver:$version"
    }


    object Accompanist {
        private const val version = "0.18.0"
        const val coil = "com.google.accompanist:accompanist-coil:0.15.0"
        const val insets = "com.google.accompanist:accompanist-insets:$version"
        const val insetsUi = "com.google.accompanist:accompanist-insets-ui:$version"
        const val navigationAnimation =
            "com.google.accompanist:accompanist-navigation-animation:$version"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
    }


    object Compose {
        private const val snapshot = ""
        private const val version = Version.compose

        const val animation = "androidx.compose.animation:animation:$version"
        const val foundation = "androidx.compose.foundation:foundation:$version"
        const val layout = "androidx.compose.foundation:foundation-layout:$version"
        const val iconsExtended = "androidx.compose.material:material-icons-extended:$version"
        const val material = "androidx.compose.material:material:$version"
        const val runtime = "androidx.compose.runtime:runtime:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val preview = "androidx.compose.ui:ui-tooling-preview:$version"
        const val ui = "androidx.compose.ui:ui:$version"
        const val uiUtil = "androidx.compose.ui:ui-util:$version"
        const val uiTest = "androidx.compose.ui:ui-test-junit4:$version"
        const val uiTestJunit = "androidx.compose.ui:ui-test-junit4:$version"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:$version"
    }

    object Moko {
        const val resourcesGenerator = "dev.icerock.moko:resources-generator:0.16.1"
        const val mobileMultiplatform = "dev.icerock:mobile-multiplatform:0.12.0"
    }


}

object Version {
    const val kotlin = "1.5.30"
    const val sqlDelight = "1.5.1"
    const val apollo = "3.0.0-alpha03"
    const val compose = "1.1.0-alpha04"
    const val coroutines = "1.5.2-native-mt"
    const val ktLint = "0.42.1"

    object AndroidX {
        val test = "1.4.0"
        val test_ext = "1.1.3"
    }
}

object GradleVersions {
    const val detekt = "1.17.1"
    const val androidTools = "7.1.0-alpha12"
    const val ktLint = "10.1.0"
    const val spotless = "5.14.1"
    const val googleServices = "4.3.8"
}

object GradlePlugins {
    const val detekt = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${GradleVersions.detekt}"
    const val googleServices = "com.google.gms:google-services:${GradleVersions.googleServices}"
    const val spotless = "com.diffplug.spotless:spotless-plugin-gradle:${GradleVersions.spotless}"
    const val ktLint = "org.jlleitschuh.gradle:ktlint-gradle:${GradleVersions.ktLint}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
    const val android = "com.android.tools.build:gradle:${GradleVersions.androidTools}"
    const val serialization = "org.jetbrains.kotlin:kotlin-serialization:${Version.kotlin}"
    const val apollo = "com.apollographql.apollo3:apollo-gradle-plugin:${Version.apollo}"
    const val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Version.sqlDelight}"
}
