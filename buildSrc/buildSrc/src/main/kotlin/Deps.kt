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
    const val stately = "co.touchlab:stately-common:1.1.7"
    const val statelyConcurrency = "co.touchlab:stately-concurrency:1.1.4"

    const val uuid = "com.benasher44:uuid:0.3.0"

    object Koin {
        private const val version = "3.1.2"
        const val core = "io.insert-koin:koin-core:$version"
        const val test = "io.insert-koin:koin-test:$version"
        const val android = "io.insert-koin:koin-android:$version"
        const val compose = "io.insert-koin:koin-androidx-compose:$version"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:28.0.1"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val messagingDirectBoot = "com.google.firebase:firebase-messaging-directboot"
        const val cloudMessaging = "com.google.firebase:firebase-messaging-ktx"
    }

    object OKHTTP {
        private const val version = "4.9.0"
        const val core = "com.squareup.okhttp3:okhttp:$version"
        const val logging = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Apollo {
        private const val version = Version.apollo
        const val runtimeKotlin = "com.apollographql.apollo:apollo-runtime-kotlin:$version"
        const val runtime = "com.apollographql.apollo:apollo-runtime:$version"
        const val coroutinesSupport = "com.apollographql.apollo:apollo-coroutines-support:$version"
        const val cacheSqlite = "com.apollographql.apollo:apollo-normalized-cache-sqlite:$version"
    }

    object MultiplatformSettings {
        private const val version = "0.7.7"
        const val settings = "com.russhwolf:multiplatform-settings:$version"
        const val coroutines = "com.russhwolf:multiplatform-settings-coroutines:$version"
        const val datastore = "com.russhwolf:multiplatform-settings-datastore:$version"
        const val test = "com.russhwolf:multiplatform-settings-test:$version"
    }

    object Jetpack {

        const val startup = "androidx.startup:startup-runtime:1.0.0"
        const val browser = "androidx.browser:browser:1.3.0"
        const val dataStore = "androidx.datastore:datastore-preferences:1.0.0-rc01"
        const val crypto = "androidx.security:security-crypto-ktx:1.1.0-alpha03"
        const val biometric = "androidx.biometric:biometric-ktx:1.2.0-alpha03"
        const val work = "androidx.work:work-runtime-ktx:2.7.0-alpha04"
        const val workMultiProcess = "androidx.work:work-multiprocess:2.7.0-alpha04"
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
            val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core"
            val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android"
            val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test"
        }
    }


    object Ktor {
        const val bom = "io.ktor:ktor-bom:1.6.0"

        const val clientCore = "io.ktor:ktor-client-core"
        const val clientJson = "io.ktor:ktor-client-json"
        const val clientLogging = "io.ktor:ktor-client-logging"
        const val clientSerialization = "io.ktor:ktor-client-serialization"
        const val clientAndroid = "io.ktor:ktor-client-android"
        const val clientEncoding = "io.ktor:ktor-client-encoding"
//        val clientApache = "io.ktor:ktor-client-apache:${Versions.ktor}"
//        val slf4j = "org.slf4j:slf4j-simple:${Versions.slf4j}"
//        val clientIos = "io.ktor:ktor-client-ios:${Versions.ktor}"
//        val clientCio = "io.ktor:ktor-client-cio:${Versions.ktor}"
//        val clientJs = "io.ktor:ktor-client-js:${Versions.ktor}"
    }

    object SqlDelight {
        private const val version = Version.sqlDelight
        const val runtime = "com.squareup.sqldelight:runtime:$version"
        const val coroutineExtensions = "com.squareup.sqldelight:coroutines-extensions:$version"
        const val androidDriver = "com.squareup.sqldelight:android-driver:$version"
        const val iosDriver = "com.squareup.sqldelight:native-driver:$version"
    }

    object Arrow {
        private const val version = Version.arrow
        const val STACK = "io.arrow-kt:arrow-stack:$version"
        const val CORE = "io.arrow-kt:arrow-core"
        const val FX = "io.arrow-kt:arrow-fx"
        const val MTL = "io.arrow-kt:arrow-mtl"
        const val SYNTAX = "io.arrow-kt:arrow-syntax"
        const val FX_MTL = "io.arrow-kt:arrow-fx-mtl"
        const val COROUTINES = "io.arrow-kt:arrow-fx-coroutines"
        const val OPTICS = "io.arrow-kt:arrow-optics"
        const val META = "io.arrow-kt:arrow-meta"
    }

    object Accompanist {
        private const val version = "0.13.0"
        const val coil = "com.google.accompanist:accompanist-coil:$version"
        const val insets = "com.google.accompanist:accompanist-insets:$version"
        const val insetsUi = "com.google.accompanist:accompanist-insets-ui:$version"
    }

    object Activity {
        const val activityCompose = "androidx.activity:activity-compose:1.3.0-rc01"
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
        const val tooling = "androidx.compose.ui:ui-tooling:1.0.0-beta09"
        const val preview = "androidx.compose.ui:ui-tooling-preview:$version"
        const val ui = "androidx.compose.ui:ui:$version"
        const val uiUtil = "androidx.compose.ui:ui-util:$version"
        const val uiTest = "androidx.compose.ui:ui-test-junit4:$version"
    }

    object ConstraintLayout {
        const val constraintLayoutCompose =
            "androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha08"
    }

}

object Version {
    const val kotlin = "1.5.10"
    const val sqlDelight = "1.5.0"
    const val apollo = "2.5.9"
    const val arrow = "0.13.2"
    const val compose = "1.0.0-rc02"
    const val androidTools = "7.1.0-alpha02"
    const val coroutines = "1.5.0-native-mt"
}


object GradleVersions {
    const val DETEKT = "1.17.1"
    const val FIREBASE_CRASHLYTICS = "2.7.0"
    const val FIREBASE_PERFORMANCE = "1.4.0"
    const val GOOGLE_SERVICES = "4.3.8"
    const val KTLINT = "0.41.0"

    const val SONARQUBE = "3.0"
    const val SPOTLESS = "5.14.1"
}


object GradlePlugins{

    const val DETEKT = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.17.1"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-crashlytics-gradle:2.7.0"
    const val FIREBASE_PERFORMANCE = "com.google.firebase:perf-plugin:1.4.0"
    const val GOOGLE_SERVICES = "com.google.gms:google-services:4.3.8"
    const val SPOTLESS = "com.diffplug.spotless:spotless-plugin-gradle:5.14.1"
    const val KTLINT = "org.jlleitschuh.gradle:ktlint-gradle:10.1.0"
}
