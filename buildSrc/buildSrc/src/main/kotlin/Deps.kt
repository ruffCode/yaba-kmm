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
    const val kermit = "co.touchlab:kermit:_"
    const val stately = "co.touchlab:stately-common:_"
    const val statelyConcurrency = "co.touchlab:stately-concurrency:_"
    const val desugar = "com.android.tools:desugar_jdk_libs:_"
    const val uuid = "com.benasher44:uuid:_"
    const val turbine = "app.cash.turbine:turbine:_"
    const val junit = "junit:junit:_"
    const val robolectric = "org.robolectric:robolectric:_"
    const val logCat = "com.squareup.logcat:logcat:_"

    object Koin {
        private const val version = "_"
        const val core = "io.insert-koin:koin-core:_"
        const val test = "io.insert-koin:koin-test:_"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:_"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val messagingDirectBoot = "com.google.firebase:firebase-messaging-directboot"
        const val cloudMessaging = "com.google.firebase:firebase-messaging-ktx"
    }

    object Apollo {
        private const val version = Version.apollo
        const val runtime = "com.apollographql.apollo3:apollo-runtime:$version"
        const val adapters = "com.apollographql.apollo3:apollo-adapters:$version"
    }

    object KotlinX {

        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:_"

        object Coroutines {
            private const val version = Version.coroutines
            const val bom = "org.jetbrains.kotlinx:kotlinx-coroutines-bom:_"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:_"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:_"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:_"
        }
    }

    object AndroidXTest {
        val core = "androidx.test:core:${Version.AndroidX.test}"
        val junit = "androidx.test.ext:junit:${Version.AndroidX.test_ext}"
        val runner = "androidx.test:runner:${Version.AndroidX.test}"
        val rules = "androidx.test:rules:${Version.AndroidX.test}"
        const val mockito = "org.mockito:mockito-inline:_"
        const val robolectric = "org.robolectric:robolectric:_"

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
        private const val version = "_"
        const val navigationAnimation =
            "com.google.accompanist:accompanist-navigation-animation:_"
    }


    object Moko {
        const val resourcesGenerator = "dev.icerock.moko:resources-generator:0.17.2"
        const val mobileMultiplatform = "dev.icerock:mobile-multiplatform:0.12.0"
    }


}

object Version {
    const val kotlin = "1.7.0"
    const val sqlDelight = "1.5.5"
    const val apollo = "3.0.0"
    const val compose = "_"
    const val coroutines = "1.6.4"
    const val ktLint = "10.2.0"

    object AndroidX {
        val test = "_"
        val test_ext = "_"
    }
}

object GradleVersions {
    const val detekt = "1.18.1"
    const val androidTools = "7.4.2"
    const val ktLint = "10.2.0"
    const val spotless = "5.15.1"
    const val googleServices = "_"
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
