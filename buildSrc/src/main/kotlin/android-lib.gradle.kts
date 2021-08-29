plugins {
    id("com.android.library")
}

android {
    compileSdk = YabaAndroidConfig.compileSdk
    sourceSets.all { java.srcDir("src/$name/kotlin") }

    defaultConfig {
        minSdk = YabaAndroidConfig.minSdk
        targetSdk = YabaAndroidConfig.targetSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    lint {
        lintConfig = rootProject.file(".lint/config.xml")
        checkAllWarnings = true
        warningsAsErrors = true
        abortOnError = false
    }

    packagingOptions {
        resources {
            excludes.add("META-INF/*")
            excludes.add("META-INF/*.kotlin_module")
            excludes.add("META-INF/LGPL2.1")
        }
    }
}
