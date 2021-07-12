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
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.auth.SessionManagerAndroid

class SettingsScreenViewModel : ViewModel(), KoinComponent {

    private val sessionManager: SessionManagerAndroid by inject()
    private val log: Kermit by inject { parametersOf("SettingsScreenViewModel") }

    fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
            delay(300)
        }
    }

    fun clearAppData() {
        viewModelScope.launch {
            sessionManager.clearAppData()
            sessionManager.logout()
            delay(100)
        }
    }

    init {

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            log.d { "tonken $it" }
        }
    }
}
