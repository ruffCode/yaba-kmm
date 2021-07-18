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
package tech.alexib.yaba.data.settings

import co.touchlab.stately.ensureNeverFrozen
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface AppSettings {
    fun theme(): Flow<Theme>
    suspend fun setTheme(theme: Theme)

    class Impl(
        private val flowSettings: FlowSettings,
    ) : AppSettings {
        override fun theme(): Flow<Theme> =
            flowSettings.getStringFlow(THEME_KEY, Theme.SYSTEM.name).map {
                Theme.valueOf(it)
            }.distinctUntilChanged()

        override suspend fun setTheme(theme: Theme) {
            flowSettings.putString(THEME_KEY, theme.name)
        }

        companion object {
            const val THEME_KEY = "app_theme"
        }

        init {
            ensureNeverFrozen()
        }
    }
}

enum class Theme(val displayName: String) {
    DARK("Dark"),
    LIGHT("Light"),
    SYSTEM("System default")
}
