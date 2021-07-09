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
package tech.alexib.yaba.kmm.di

import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.alexib.yaba.kmm.BiometricSettings
import tech.alexib.yaba.kmm.YabaAppSettings
import tech.alexib.yaba.kmm.auth.BiometricsManager
import tech.alexib.yaba.kmm.auth.BiometricsManagerAndroid
import tech.alexib.yaba.kmm.auth.CipherWrapper
import tech.alexib.yaba.kmm.auth.SessionManagerAndroid
import tech.alexib.yaba.kmm.data.db.AppSettings
import tech.alexib.yaba.kmm.data.db.DriverFactory

actual val platformModule: Module = module {
    single { createIoDispatcher() }
    single { CipherWrapper() }
    single { SessionManagerAndroid(get(), get()) }
    single<BiometricsManager> { BiometricsManagerAndroid() }
    single<AppSettings> { YabaAppSettings() }
    single { BiometricSettings() }
    single<SqlDriver> { DriverFactory(get(), getWith("SqlDelight")).createDriver() }
    val baseKermit = Kermit(LogcatLogger()).withTag("Yaba")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}

internal fun createIoDispatcher() = Dispatchers.Default
