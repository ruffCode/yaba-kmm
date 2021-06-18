import Version.androidTools

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

//dependencies{
//    implementation("com.android.tools.build:builder:$androidTools")
//    implementation("com.android.tools.build:builder-model:$androidTools")
//    implementation("com.android.tools.build:gradle:$androidTools")
//}