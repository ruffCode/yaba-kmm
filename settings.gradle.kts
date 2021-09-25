
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
    plugins {
        id("de.fayard.refreshVersions").version("0.22.0-SNAPSHOT")
    }
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
    }
}
plugins {
    id("com.gradle.enterprise") version "3.7"
    id("de.fayard.refreshVersions")
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

refreshVersions {
}
gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service'"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "Yaba_KMM"
include(":androidApp")
include(":base")
include(":data")
include(":data:domain")
include(":data:network")
include(":data:db")
