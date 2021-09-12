plugins {
    id("android-lib")
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("static-analysis")
    id("com.apollographql.apollo3")
}

dependencies {
    commonMainImplementation(Lib.Apollo.runtimeKotlin)
    commonMainImplementation(Lib.Apollo.adapters)
    commonMainImplementation(projects.data.domain)
}

configure<com.apollographql.apollo3.gradle.api.ApolloExtension> {
    customScalarsMapping.set(
        mapOf(
            "UUID" to "com.benasher44.uuid.Uuid",
            "LocalDate" to "kotlinx.datetime.LocalDate"
        )
    )
    packageName.set("yaba.schema")
}
