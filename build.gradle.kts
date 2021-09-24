buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.30")
        classpath("dev.icerock.moko:resources-generator:0.16.1")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.1")
        classpath("com.apollographql.apollo3:apollo-gradle-plugin:${Version.apollo}")
        classpath("com.google.gms:google-services:4.3.10")
    }
}

allprojects {
    configurations.configureEach {
        resolutionStrategy {
            eachDependency {
                when (requested.name) {
                    "kotlinx-coroutines-core" -> useVersion(Version.coroutines)
                }
            }
        }
    }

}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    afterEvaluate {
        project.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()
            ?.let { kmpExt ->
                kmpExt.sourceSets.run {
                    all {
                        languageSettings.apply {
                            progressiveMode = true
                            optIn("kotlin.RequiresOptIn")
                            optIn("kotlin.time.ExperimentalTime")
                            optIn("kotlinx.serialization.ExperimentalSerializationApi")
                            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                        }
                    }
                }
            }
//        tasks.named("check").configure {
//            dependsOn(tasks.getByName("ktlintCheck"))
//        }
    }
}

tasks.register("clean", Delete::class).configure {
    group = "build"
    delete(rootProject.buildDir)
}
//
// afterEvaluate {
//     // We install the hook at the first occasion
//     tasks["clean"].dependsOn(tasks.getByName("addKtlintFormatGitPreCommitHook"))
// }
