plugins {
    id("kotlin-multiplatform")
    id("base-convention")
    id("com.android.library")
    id("android-base-convention")
    id("android-lib")
    id("dev.icerock.mobile.multiplatform.android-manifest")
}

kotlin {
    android()
//    val isMac = System.getProperty("os.name").startsWith("Mac")
//    if (isMac) {
//        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) {
//            iosArm64("ios")
//        } else {
//            iosX64("ios") {}
//        }
//    }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${Version.kotlin}")
                implementation(Lib.KotlinX.Coroutines.core)
                api(Lib.kermit)
                implementation(Lib.stately)
                implementation(Lib.statelyConcurrency)
                implementation(Lib.uuid)
                api(Lib.Koin.core)
                implementation(Lib.KotlinX.dateTime)
            }
        }
        named("commonTest") {
            dependencies {
                implementation(Lib.KotlinTest.common)
                implementation(Lib.KotlinTest.annotations)
                implementation(Lib.Koin.test)
                implementation(Lib.turbine)
                implementation(Lib.KotlinX.Coroutines.test) {
                    exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-debug")
                }
            }
        }

        named("androidMain") {
            dependencies {
                implementation(kotlin("stdlib", Version.kotlin))

                implementation(Lib.KotlinX.Coroutines.android)

            }
        }
        named("androidTest") {
            dependencies {
                implementation(Lib.KotlinTest.jvm)
                implementation(Lib.KotlinTest.junit)
                implementation(Lib.AndroidXTest.core)
                implementation(Lib.AndroidXTest.junit)
                implementation(Lib.AndroidXTest.runner)
                implementation(Lib.AndroidXTest.rules)
                implementation(Lib.KotlinX.Coroutines.test)
                implementation(Lib.robolectric)
            }
        }


//        if (isMac) {
//
//            named("iosMain") {
//                dependencies {
//                    implementation(Lib.KotlinX.Coroutines.core) {
//                        version {
//                            strictly(Version.coroutines)
//                        }
//                    }
//                }
//            }
//            named("iosTest") {}
//
//        }
    }
}

