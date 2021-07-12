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
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@ExperimentalSettingsApi
abstract class AppSettings {

    protected abstract val flowSettings: FlowSettings

    fun userId(): Flow<Uuid?> = flowSettings.getStringOrNullFlow(USER_ID).map { userId ->
        if (!userId.isNullOrEmpty()) {
            uuidFrom(userId)
        } else null
    }

    init {
        ensureNeverFrozen()
    }

    fun showOnboarding(): Flow<Boolean> = flowSettings.getBooleanFlow(SHOW_ONBOARDING, true)
    fun isLoggedIn(): Flow<Boolean> = token().map { !it.isNullOrEmpty() }

    fun token(): Flow<String?> = flowSettings.getStringOrNullFlow(AUTH_TOKEN)

    private val authTokenFlow = MutableStateFlow<String?>(null)

    val authToken: StateFlow<String?>
        get() = authTokenFlow

    suspend fun clearAuthToken() {
        flowSettings.putString(AUTH_TOKEN, "")
        authTokenFlow.emit(null)
    }

    private suspend fun setShowOnboarding(show: Boolean) {
        flowSettings.putBoolean(SHOW_ONBOARDING, show)
    }

    suspend fun clearAppSettings() {
        flowSettings.clear()
    }

    suspend fun setUserId(userId: Uuid) {
        flowSettings.putString(USER_ID, userId.toString())
    }

    suspend fun setToken(token: String) {
        authTokenFlow.value = token
        flowSettings.putString(AUTH_TOKEN, token)
        setShowOnboarding(false)
    }

    suspend fun clearUserId() {
        flowSettings.putString(USER_ID, "")
    }

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val USER_ID = "USER_ID"
        private const val SHOW_ONBOARDING = "SHOW_ONBOARDING"
    }
}
