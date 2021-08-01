plugins {
    id("android-lib")
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("static-analysis")
    id("com.squareup.sqldelight")
}

dependencies {
    commonMainApi(Lib.SqlDelight.runtime)
    commonMainApi(Lib.SqlDelight.coroutineExtensions)
    androidMainApi(Lib.SqlDelight.androidDriver)
    androidMainApi(Lib.SqlDelight.jvm)
    androidTestImplementation(Lib.SqlDelight.jvm)
    commonMainImplementation(projects.base)
    commonMainImplementation(projects.data.domain)
}

sqldelight {
    database("YabaDb") {
        packageName = "tech.alexib.yaba.data.db"
        schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
        dialect = "sqlite:3.25"
        linkSqlite = true
    }
}
