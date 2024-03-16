plugins {
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
}

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                optIn("com.russhwolf.settings.ExperimentalSettingsApi")
                optIn(
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
    commonMainImplementation(Koin.workManager)

    with(RussHWolf) {
        commonMainImplementation(multiplatformSettings.settings)
        commonMainImplementation(multiplatformSettings.coroutines)
        commonTestImplementation(multiplatformSettings.test)
        androidMainImplementation(multiplatformSettings.dataStore)
    }
    with(AndroidX) {
        androidMainImplementation(dataStore.preferences)
        androidMainImplementation(security.cryptoKtx)
        androidMainImplementation(biometric.ktx)
        androidMainImplementation(work.runtimeKtx)
        androidMainImplementation(work.multiprocess)
        androidMainApi(compose.runtime)
    }
}
android {
    namespace = "tech.alexib.yaba.data"
}

tasks {
    ktlintFormat {
        doLast {
            delete("src/main")
            delete("src/test")
        }
    }
}
