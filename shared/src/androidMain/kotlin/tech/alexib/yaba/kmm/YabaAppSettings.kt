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
package tech.alexib.yaba.kmm

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.russhwolf.settings.datastore.DataStoreSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.db.AppSettings

class YabaAppSettings : AppSettings(), KoinComponent {

    private val appContext: Context by inject()
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "yaba-settings"
    )
    private val dataStore: DataStore<Preferences> = appContext.dataStore
    private val log: Kermit by inject { parametersOf("YabaAppSettings") }

    private val dataStoreSettings = DataStoreSettings(dataStore)
    override val flowSettings = dataStoreSettings

    init {
        ensureNeverFrozen()
    }
}
