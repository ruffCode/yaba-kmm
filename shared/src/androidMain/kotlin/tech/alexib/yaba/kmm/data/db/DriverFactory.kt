package tech.alexib.yaba.kmm.data.db

import android.app.Application
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import tech.alexib.yaba.data.db.YabaDb

actual class DriverFactory(private val context: Application){
    actual fun createDriver():SqlDriver {
        return AndroidSqliteDriver(YabaDb.Schema, context, "yaba.db")
    }
}
