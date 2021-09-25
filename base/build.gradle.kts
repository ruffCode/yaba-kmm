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
tasks {
    ktlintFormat {
        doLast {
            delete("src/main")
        }
    }
}
