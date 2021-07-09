pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.0"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service'"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "Yaba_KMM"
include(":androidApp")
include(":shared")
