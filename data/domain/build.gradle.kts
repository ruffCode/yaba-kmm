plugins {
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
}

dependencies {
    commonMainApi(projects.base)
}
android {
    namespace = "tech.alexib.yaba.data.domain"
}
