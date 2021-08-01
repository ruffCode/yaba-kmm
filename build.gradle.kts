buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.10")
        classpath("dev.icerock.moko:resources-generator:0.16.1")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.1")
        classpath("com.apollographql.apollo3:apollo-gradle-plugin:3.0.0-alpha03")
        classpath("com.google.gms:google-services:4.3.8")
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
                            useExperimentalAnnotation("kotlin.RequiresOptIn")
                            useExperimentalAnnotation("kotlin.time.ExperimentalTime")
                            useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
                            useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
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
