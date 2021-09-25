plugins {
    id("base-convention")
    id("com.android.library")
    id("android-base-convention")
}

android {
    sourceSets.all { java.srcDir("src/$name/kotlin") }

    lint {
        lintConfig = rootProject.file(".lint/config.xml")
        checkAllWarnings = true
        warningsAsErrors = true
        abortOnError = false
    }
}
