plugins {
    id("android-lib")
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("static-analysis")
    id("com.apollographql.apollo3")
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
    commonMainImplementation(Lib.Apollo.adapters)
    commonMainImplementation(projects.data.domain)
}

configure<com.apollographql.apollo3.gradle.api.ApolloExtension> {
    customScalarsMapping.set(
        mapOf(
//            "timestamptz" to "kotlinx.datetime.Instant",
            "UUID" to "com.benasher44.uuid.Uuid",
            "LocalDate" to "kotlinx.datetime.LocalDate"
//            "smallint" to "kotlin.Int"
        )
    )
    packageName.set("yaba.schema")
}
