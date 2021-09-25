import com.android.build.gradle.BaseExtension

configure<BaseExtension> {
    compileSdkVersion(YabaAndroidConfig.compileSdk)

    defaultConfig {
        minSdk = YabaAndroidConfig.minSdk
        targetSdk = YabaAndroidConfig.targetSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        resources {
            excludes.add("META-INF/*")
            excludes.add("META-INF/*.kotlin_module")
            excludes.add("META-INF/LGPL2.1")
        }
    }
}
