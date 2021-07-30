plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

kotlin {
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}

dependencies {
    api(Lib.Moko.mobileMultiplatform)
    api(GradlePlugins.kotlin)
    api(GradlePlugins.android)
    api("com.android.tools.build:builder:7.1.0-alpha05")
    api("com.android.tools.build:builder-model:7.1.0-alpha05")
//    api(GradlePlugins.detekt)
    api(GradlePlugins.spotless)
    api(GradlePlugins.ktLint)
}
