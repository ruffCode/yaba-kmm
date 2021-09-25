plugins {
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("com.squareup.sqldelight")
}

dependencies {
    with(Lib.SqlDelight) {
        commonMainApi(runtime)
        commonMainApi(coroutineExtensions)
        androidMainApi(androidDriver)
        androidMainApi(jvm)
        androidTestImplementation(jvm)
    }
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
tasks {
    ktlintFormat {
        doLast {
            delete("src/main")
        }
    }
}
