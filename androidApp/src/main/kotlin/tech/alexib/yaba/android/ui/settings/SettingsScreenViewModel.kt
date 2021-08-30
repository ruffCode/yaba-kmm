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
package tech.alexib.yaba.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.fcm.PushTokenManager
import tech.alexib.yaba.data.interactor.ClearAppData
import tech.alexib.yaba.data.repository.AuthRepository
import tech.alexib.yaba.data.settings.AppSettings
import tech.alexib.yaba.data.settings.Theme
import tech.alexib.yaba.util.stateInDefault

class SettingsScreenViewModel : ViewModel(), KoinComponent {

    private val appSettings: AppSettings by inject()
    private val authRepository: AuthRepository by inject()
    private val pushTokenManager: PushTokenManager by inject()
    private val clearAppData: ClearAppData by inject()
    private val log: Kermit by inject { parametersOf("SettingsScreenViewModel") }

    val state: StateFlow<SettingsScreenState> = appSettings.theme().mapLatest {
        SettingsScreenState(it)
    }.distinctUntilChanged().stateInDefault(viewModelScope, SettingsScreenState.Empty)

    fun logout() {
        viewModelScope.launch {
            deleteToken()
            authRepository.logout()
        }
    }

    fun clearAppData() {
        viewModelScope.launch {
            deleteToken()
            clearAppData.executeSync(Unit)
            authRepository.logout()
        }
    }

    private suspend fun deleteToken() {
        withContext(Dispatchers.Default) {
            val instance = FirebaseMessaging.getInstance()
            instance.token.addOnCompleteListener { task ->
                task.result?.let {
                    pushTokenManager.deleteToken(it)
                    Firebase.messaging.isAutoInitEnabled = false
                    Firebase.analytics.setAnalyticsCollectionEnabled(false)
                    Firebase.messaging.deleteToken()
                }
            }
        }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            appSettings.setTheme(theme)
        }
    }
}
