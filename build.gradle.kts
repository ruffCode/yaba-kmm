buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("com.android.tools.build:gradle:7.1.0-alpha10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.21")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.1")
        classpath("com.apollographql.apollo:apollo-gradle-plugin:2.5.9")
        classpath("com.google.gms:google-services:4.3.10")
    }
}

plugins {
    id("com.diffplug.spotless") version "5.14.1"
    // id("io.gitlab.arturbosch.detekt") version GradleVersions.DETEKT apply false
    id("org.jlleitschuh.gradle.ktlint-idea").version("10.1.0")
    id("org.jlleitschuh.gradle.ktlint").version("10.1.0")
}
allprojects {
    repositories {
        google()
        mavenCentral()
    }

    apply<org.jlleitschuh.gradle.ktlint.KtlintPlugin>()
    // apply<io.gitlab.arturbosch.detekt.DetektPlugin>()
    apply<com.diffplug.gradle.spotless.SpotlessPlugin>()

    // configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    //     // input = files("src")
    //     // // input.setFrom("src/commonMain/kotlin", "src/androidMain/kotlin", "src/iosMain/kotlin")
    //     // config = files("$rootDir/detekt.yml")
    //     //
    //     // baseline = file("$rootDir/detekt/detekt-baseline.xml")
    //     //
    //     // autoCorrect = true
    //     // parallel = true
    //     configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    //         input.setFrom("src/commonMain/kotlin", "src/androidMain/kotlin", "src/iosMain/kotlin")
    //     }
    // }

    spotless {
        java {
            target("**/*.java")
            googleJavaFormat().aosp()
            removeUnusedImports()
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
        kotlin {
            target("**/*.kt")
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
            targetExclude("$buildDir/**/*.kt")
            targetExclude("Deps.kt")
            targetExclude("**/build/**")
            targetExclude("**/generated/**")
            targetExclude("spotless/**")
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint("0.41.0")
        }
    }
    ktlint {
        // debug.set(true)
        // verbose.set(true)
        android.set(true)
        outputToConsole.set(true)
        outputColorName.set("BLUE")
        ignoreFailures.set(true)
        // enableExperimentalRules.set(true)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
        filter {
            exclude("**/generated/**")
            exclude("$buildDir/**/*.kt")
            include("**/kotlin/**")
        }
        additionalEditorconfigFile.set(file("${rootProject.rootDir}/.editorConfig"))
    }

    afterEvaluate {
        tasks.named("check").configure {
            dependsOn(tasks.getByName("ktlintCheck"))
        }
    }
}
//
// afterEvaluate {
//     // We install the hook at the first occasion
//     tasks["clean"].dependsOn(tasks.getByName("addKtlintFormatGitPreCommitHook"))
// }
