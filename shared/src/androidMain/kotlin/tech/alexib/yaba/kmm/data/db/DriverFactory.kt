package tech.alexib.yaba.kmm.data.db

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import co.touchlab.kermit.Kermit
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.logs.LogSqliteDriver
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.BuildConfig

actual class DriverFactory(private val context: Context, private val log: Kermit) {
    actual fun createDriver(): SqlDriver {
        val driver = AndroidSqliteDriver(YabaDb.Schema, context, "yaba.db",
            callback = object : AndroidSqliteDriver.Callback(YabaDb.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }

            })
        return if (BuildConfig.DEBUG) {
            LogSqliteDriver(driver) { log.d { it } }
        } else driver
    }
}
