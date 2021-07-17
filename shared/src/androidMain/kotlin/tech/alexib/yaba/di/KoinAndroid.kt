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
package tech.alexib.yaba.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.work.WorkManager
import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.alexib.yaba.BiometricSettings
import tech.alexib.yaba.data.auth.BiometricsManager
import tech.alexib.yaba.data.auth.BiometricsManagerAndroid
import tech.alexib.yaba.data.auth.CipherWrapper
import tech.alexib.yaba.data.auth.SessionManagerAndroid
import tech.alexib.yaba.data.settings.AuthSettings
import tech.alexib.yaba.data.db.DriverFactory
import java.io.File

actual val platformModule: Module = module {
    single { createIoDispatcher() }
    single<FlowSettings> {
        val context: Context = get()
        val ioDispatcher: CoroutineDispatcher = get()
        val dataStore = PreferenceDataStoreFactory.create(scope = CoroutineScope(ioDispatcher)) {
            File(context.filesDir, "yaba_settings.preferences_pb")
        }
        DataStoreSettings(dataStore)
    }
    single { CipherWrapper() }
    single { SessionManagerAndroid(get(), get()) }
    single<BiometricsManager> { BiometricsManagerAndroid() }
    single<AuthSettings> { AuthSettings.Impl(get()) }
    single<BiometricSettings> { BiometricSettings.Impl(get(), get()) }
    single<SqlDriver> { DriverFactory(get(), getWith("SqlDelight")).createDriver() }
    single { WorkManager.getInstance(get()) }
    val baseKermit = Kermit(LogcatLogger()).withTag("Yaba")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}

internal fun createIoDispatcher() = Dispatchers.IO
