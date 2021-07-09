package tech.alexib.yaba.kmm.data.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import tech.alexib.yaba.data.db.YabaDb

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(YabaDb.Schema, "YabaDb")
    }
}
