object AndroidConfig {
    const val APPLICATION_ID = "tech.alexib.groomo"
    const val COMPILE_SDK = 30
    const val MIN_SDK = 29
    const val TARGET_SDK = 30
    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.1.0"
    const val BUILD_TOOLS = "30.0.3"
    val COMPILER_ARGS = listOf(
        "-Xinline-classes","-Xopt-in=kotlin.RequiresOptIn",

        "-Xskip-prerelease-check",
        "-Xuse-experimental=kotlin.Experimental",
        "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
        "-Xuse-experimental=androidx.compose.material.ExperimentalMaterialApi"
    )
}
