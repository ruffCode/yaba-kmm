plugins {
    id("android-lib")
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("static-analysis")
    id("com.apollographql.apollo")
}

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("com.apollographql.apollo.api.ApolloExperimental")
            }
        }
    }
}
dependencies {
    commonMainImplementation(Lib.Apollo.runtimeKotlin)
    commonMainImplementation(projects.data.domain)
}
configure<com.apollographql.apollo.gradle.api.ApolloExtension> {
    generateKotlinModels.set(true)
    customTypeMapping.set(
        mapOf(
            "timestamptz" to "kotlinx.datetime.Instant",
            "uuid" to "com.benasher44.uuid.Uuid",
            "date" to "kotlinx.datetime.LocalDate",
            "smallint" to "kotlin.Int"
        )
    )
}
