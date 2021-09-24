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
    api("com.android.tools.build:builder:${GradleVersions.androidTools}")
    api("com.android.tools.build:builder-model:${GradleVersions.androidTools}")
//    api("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.0-alpha07")
//    api(GradlePlugins.detekt)
    api(GradlePlugins.spotless)
    api(GradlePlugins.ktLint)
}
