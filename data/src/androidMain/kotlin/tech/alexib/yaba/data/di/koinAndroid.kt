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
package tech.alexib.yaba.data.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.alexib.yaba.data.biometrics.BiometricSettings
import tech.alexib.yaba.data.biometrics.BiometricSettingsImpl
import tech.alexib.yaba.data.biometrics.BiometricsManager
import tech.alexib.yaba.data.biometrics.CipherWrapper
import tech.alexib.yaba.data.settings.AppSettings
import tech.alexib.yaba.data.settings.AuthSettings
import tech.alexib.yaba.data.task.UpdateTransactionsWorker
import tech.alexib.yaba.data.task.UserPushTokenWorker
import tech.alexib.yaba.di.CoreDependencies
import tech.alexib.yaba.di.CoreDependencies.ioDispatcherQualifier
import tech.alexib.yaba.util.getWith
import java.io.File

actual val platformModule: Module = module {
    val baseKermit = Kermit(LogcatLogger()).withTag("Yaba")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
    single<CoroutineDispatcher>(CoreDependencies.mainDispatcherQualifier) { Dispatchers.Main }
    single(ioDispatcherQualifier) { Dispatchers.IO }
    single<FlowSettings> {
        val context: Context = get()
        val ioDispatcher: CoroutineDispatcher = get(ioDispatcherQualifier)
        val dataStore = PreferenceDataStoreFactory.create(scope = CoroutineScope(ioDispatcher)) {
            File(context.filesDir, "yaba_settings.preferences_pb")
        }
        DataStoreSettings(dataStore)
    }
    single<AuthSettings> { AuthSettings.Impl(get()) }
    single<BiometricSettings> { BiometricSettingsImpl(get(), get()) }
    single { BiometricsManager(get(), getWith("BiometricsManager"), get(), get(), get()) }
    single { CipherWrapper() }
//    single {
//        val context: Context = get()
//        WorkManager.getInstance(context)
//    }
    single<AppSettings> { AppSettings.Impl(get()) }
    worker { UpdateTransactionsWorker(get(), get(), get()) }
    worker { UserPushTokenWorker(get(), get(), get()) }
}
