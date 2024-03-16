buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(GradlePlugins.serialization)
//        classpath(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
        classpath(Square.sqlDelight.gradlePlugin)
        classpath(GradlePlugins.apollo)
        classpath(Google.playServicesGradlePlugin)
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
plugins {
    id("org.jlleitschuh.gradle.ktlint")
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

afterEvaluate {
    // We install the hook at the first occasion
    tasks["clean"].dependsOn(tasks.getByName("addKtlintFormatGitPreCommitHook"))
}
