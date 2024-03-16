
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
    plugins {
        id("de.fayard.refreshVersions").version("0.51.0")
////                                # available:"0.52.0-SNAPSHOT")
////                                # available:"0.60.0")
////                                # available:"0.60.1")
////                                # available:"0.60.2-SNAPSHOT")
////                                # available:"0.60.2")
////                                # available:"0.60.3")
////                                # available:"0.60.4")
////                                # available:"0.60.5")
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
    id("com.gradle.enterprise") version "3.16.2"
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
