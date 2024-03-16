plugins {
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("com.apollographql.apollo3")
}

dependencies {
    with(Lib.Apollo) {
        commonMainImplementation(runtime)
        commonMainImplementation(adapters)
    }
    commonMainImplementation(projects.data.domain)
}
android {
    namespace = "tech.alexib.yaba.data.network"
}

configure<com.apollographql.apollo3.gradle.api.ApolloExtension> {
    customScalarsMapping.set(
        mapOf(
            "UUID" to "com.benasher44.uuid.Uuid",
            "LocalDate" to "kotlinx.datetime.LocalDate"
        )
    )
    packageName.set("yaba.schema")
    generateSchema.set(true)
}
