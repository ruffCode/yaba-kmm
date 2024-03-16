plugins {
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
}

dependencies {
    with(KotlinX) {
        commonMainApi(serialization.core)
        commonMainApi(serialization.json)
    }
}
android {
    namespace = "tech.alexib.yaba.base"
}
tasks {
    ktlintFormat {
        doLast {
            delete("src/main")
        }
    }
}
