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
package tech.alexib.yaba.data.db.di

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.alexib.yaba.data.db.DriverFactory
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.util.getWith

actual val dbPlatformModule: Module = module {
    single<SqlDriver> { DriverFactory(get(), getWith("SqlDelight")).createDriver() }
}

actual val dbPlatformTestModule: Module = module {
    single<SqlDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            YabaDb.Schema.create(this)
        }
    }
}
