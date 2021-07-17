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

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AppSettings {
    fun userId(): Flow<Uuid?>
    fun showOnboarding(): Flow<Boolean>
    fun token(): Flow<String?>
    suspend fun clearAuthToken()
    suspend fun clearAppSettings()
    suspend fun setUserId(userId: Uuid)
    suspend fun setToken(token: String)
    suspend fun clearUserId()

    class Impl(
        private val flowSettings: FlowSettings
    ) : AppSettings {

        init {
            ensureNeverFrozen()
        }
        override fun userId(): Flow<Uuid?> =
            flowSettings.getStringOrNullFlow(USER_ID).map { userId ->
                if (!userId.isNullOrEmpty()) {
                    uuidFrom(userId)
                } else null
            }

        override fun showOnboarding(): Flow<Boolean> =
            flowSettings.getBooleanFlow(SHOW_ONBOARDING, true)

        override fun token(): Flow<String?> = flowSettings.getStringOrNullFlow(AUTH_TOKEN)

        override suspend fun clearAuthToken() {
            flowSettings.putString(AUTH_TOKEN, "")
        }

        override suspend fun clearAppSettings() {
            flowSettings.clear()
        }

        override suspend fun setUserId(userId: Uuid) {
            flowSettings.putString(USER_ID, userId.toString())
        }

        override suspend fun setToken(token: String) {
            flowSettings.putString(AUTH_TOKEN, token)
            setShowOnboarding(false)
        }

        override suspend fun clearUserId() {
            flowSettings.putString(USER_ID, "")
        }

        private suspend fun setShowOnboarding(show: Boolean) {
            flowSettings.putBoolean(SHOW_ONBOARDING, show)
        }

        companion object {
            private const val AUTH_TOKEN = "AUTH_TOKEN"
            private const val USER_ID = "USER_ID"
            private const val SHOW_ONBOARDING = "SHOW_ONBOARDING"
        }
    }
}
