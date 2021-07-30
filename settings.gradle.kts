enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
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
include(":base")
include(":data")
include(":data:domain")

include(":data:network")


include(":data:db")

