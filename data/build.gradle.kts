plugins {
    id("android-lib")
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("static-analysis")
}

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsApi")
                useExperimentalAnnotation(
                    "com.russhwolf.settings.ExperimentalSettingsImplementation"
                )
            }
        }
    }
}
dependencies {
    commonMainApi(projects.base)
    commonMainImplementation(projects.data.domain)
    commonMainImplementation(projects.data.network)
    commonMainImplementation(projects.data.db)
    commonTestImplementation(Lib.turbine)
    commonMainImplementation(Lib.Koin.work)
    commonMainImplementation(Lib.MultiplatformSettings.settings)
    commonMainImplementation(Lib.MultiplatformSettings.coroutines)
    commonTestImplementation(Lib.MultiplatformSettings.test)
    androidMainImplementation(Lib.MultiplatformSettings.datastore)
    androidMainImplementation(Lib.AndroidX.dataStore)
    androidMainImplementation(Lib.AndroidX.crypto)
    androidMainImplementation(Lib.AndroidX.biometric)
    androidMainImplementation(Lib.AndroidX.work)
    androidMainImplementation(Lib.AndroidX.workMultiProcess)
}

tasks {
    ktlintFormat {
        doLast {
            delete("src/main")
            delete("src/test")
        }
    }
}
