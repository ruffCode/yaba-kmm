/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.alexib.yaba.data.db

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import co.touchlab.kermit.Kermit
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.logs.LogSqliteDriver
import tech.alexib.yaba.BuildConfig

actual class DriverFactory(private val context: Context, private val log: Kermit) {
    actual fun createDriver(): SqlDriver {
        val driver = AndroidSqliteDriver(
            YabaDb.Schema,
            context,
            "yaba.db",
            callback = object : AndroidSqliteDriver.Callback(YabaDb.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            }
        )
        return if (BuildConfig.DEBUG) {
            LogSqliteDriver(driver) { log.d { it } }
        } else driver
    }
}
