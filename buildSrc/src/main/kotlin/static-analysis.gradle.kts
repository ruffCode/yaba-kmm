plugins {
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jlleitschuh.gradle.ktlint-idea")
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

ktlint {
    // debug.set(true)
    // verbose.set(true)
    version.set(Version.ktLint)
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
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }

    }
    additionalEditorconfigFile.set(file("${rootProject.rootDir}/.editorConfig"))
}

spotless {
    kotlin {
        target("**/src/**/*.kt")
        licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        targetExclude("$buildDir/**/*.kt")
        targetExclude("**/generated/**")
        targetExclude("spotless/copyright.kt")
        ktlint(Version.ktLint)
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(Version.ktLint)
    }
}

detekt {
    source.setFrom(
        "src/commonMain/kotlin",
        "src/androidMain/kotlin",
        "src/iosMain/kotlin",
        "src/main/kotlin",
        "src/main/java"
    )
    config = files(rootProject.file("config/detekt.yml"))

    parallel = true
}
