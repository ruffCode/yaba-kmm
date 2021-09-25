plugins {
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
}

dependencies {
    commonMainApi(projects.base)
}
