plugins {
//    id("android-lib")
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
//    id("dev.icerock.mobile.multiplatform.android-manifest")
//    id("static-analysis")
}

dependencies {
    commonMainApi(Lib.KotlinX.Serialization.core)
    commonMainApi(Lib.KotlinX.Serialization.json)
}
tasks {
    ktlintFormat {
        doLast {
            delete("src/main")
        }
    }
}
