plugins{
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jlleitschuh.gradle.ktlint-idea")
    id("com.diffplug.spotless")
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
        exclude("**/build/**")
    }
    additionalEditorconfigFile.set(file("${rootProject.rootDir}/.editorConfig"))
}

spotless{
    kotlin {
        target("**/*.kt")
        licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        targetExclude("**/build/**")
        targetExclude("**/generated/**")
        targetExclude("spotless/**")
        ktlint(Version.ktLint)
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(Version.ktLint)
    }
}