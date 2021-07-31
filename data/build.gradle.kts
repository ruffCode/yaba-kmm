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
    commonMainImplementation(Lib.MultiplatformSettings.settings)
    commonMainImplementation(Lib.MultiplatformSettings.coroutines)
    commonTestImplementation(Lib.MultiplatformSettings.test)
    androidMainImplementation(Lib.MultiplatformSettings.datastore)
    androidMainImplementation(Lib.Jetpack.dataStore)
}
