//package tech.alexib.plugins
//
//import com.android.build.gradle.LibraryExtension
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.gradle.kotlin.dsl.apply
//import org.gradle.kotlin.dsl.get
//import org.gradle.kotlin.dsl.getByType
//
//public class YabaAndroidLibraryPlugin : Plugin<Project> {
//    override fun apply(target: Project) {
//        target.apply<Project> {
//            extensions.getByType<LibraryExtension>().apply {
//
//                compileSdk = 30
//                sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")
//                defaultConfig {
//                    minSdk = 30
//                    targetSdk = 30
//                }
//
//                configurations.apply {
//                    //        create("androidTestApi")
////        create("androidTestDebugApi")
////        create("androidTestReleaseApi")
//                    create("testApi")
//                    create("testDebugApi")
//                    create("testReleaseApi")
//                }
//            }
//        }
//    }
//}