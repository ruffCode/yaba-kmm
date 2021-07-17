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
package tech.alexib.yaba

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import co.touchlab.kermit.Kermit
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.auth.EncryptionManager
import tech.alexib.yaba.data.db.AppSettings

internal interface BiometricSettings {
    suspend fun setBioEnabled(enabled: Boolean)
    val hasPromptedForBiometrics: Flow<Boolean>
    fun isBioEnabled(): Flow<Boolean>
    val bioToken: Flow<String?>
    suspend fun bioToken()
    suspend fun handleUnsuccessfulBioLogin()
    suspend fun clear()
    fun cryptoObject(): BiometricPrompt.CryptoObject

    class Impl(
        private val flowSettings: FlowSettings,
        private val appSettings: AppSettings,
    ) : BiometricSettings {
        private val encryptionManager = EncryptionManager

        private fun String.encrypt(): String = encryptionManager.encrypt(this)
        private fun String.decrypt(): String = encryptionManager.decrypt(this)

        override suspend fun setBioEnabled(enabled: Boolean) {
            flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
            if (!enabled) {
                clearBioToken()
            } else {
                val token = appSettings.token().firstOrNull()
                if (!token.isNullOrEmpty()) {
                    setBioToken(token)
                }
            }
        }

        private suspend fun setHasPromptedBiometrics(prompted: Boolean = true) {
            flowSettings.putBoolean(HAS_PROMPTED_FOR_BIOMETRICS, prompted)
        }

        override val hasPromptedForBiometrics: Flow<Boolean> = flowSettings.getBooleanFlow(
            HAS_PROMPTED_FOR_BIOMETRICS,
            false
        )

        override fun isBioEnabled(): Flow<Boolean> =
            flowSettings.getBooleanFlow(IS_BIO_ENABLED, false)

        private suspend fun getBioToken() = flowSettings.getStringFlow(BIO_TOKEN, "").first()

        override val bioToken = flowSettings.getStringOrNullFlow(BIO_TOKEN)
        override suspend fun bioToken() {
            val encryptedToken = getBioToken()
            if (encryptedToken.isNotBlank()) {
                val decryptedToken = encryptedToken.decrypt()
                if (decryptedToken.isNotEmpty()) {
                    appSettings.setToken(decryptedToken)
                }
            }
        }

        private suspend fun disableBio() {
            setBioEnabled(false)
            setHasPromptedBiometrics(false)
        }

        private suspend fun setBioToken(token: String) {
            if (isBioEnabled().first()) {
                flowSettings.putString(BIO_TOKEN, token.encrypt())
            }
        }

        private suspend fun clearBioToken() {
            flowSettings.putString(BIO_TOKEN, "")
        }

        override suspend fun handleUnsuccessfulBioLogin() {
            disableBio()
        }

        override suspend fun clear() {
            flowSettings.clear()
        }

        override fun cryptoObject(): BiometricPrompt.CryptoObject =
            encryptionManager.getCryptoObject()

        companion object {
            private const val BIO_TOKEN = "BIO_TOKEN"
            private const val IS_BIO_ENABLED = "IS_BIO_ENABLED"
            private const val HAS_PROMPTED_FOR_BIOMETRICS = "HAS_PROMPTED_FOR_BIOMETRICS"
        }
    }
}

// internal class BiometricSettings : KoinComponent {
//
//     private val appContext: Context by inject()
//     private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
//         name = "yaba-biometric-settings"
//     )
//     private val dataStore: DataStore<Preferences> = appContext.dataStore
//     private val flowSettings: FlowSettings = DataStoreSettings(dataStore)
//     private val appSettings: AppSettings by inject()
//     private val encryptionManager = EncryptionManager
//     private val log: Kermit by inject { parametersOf("BiometricSettings") }
//     private fun String.encrypt(): String = encryptionManager.encrypt(this)
//     private fun String.decrypt(): String = encryptionManager.decrypt(this)
//
//     suspend fun setBioEnabled(enabled: Boolean) {
//         flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
//         if (!enabled) {
//             clearBioToken()
//         } else {
//             val token = appSettings.token().firstOrNull()
//             if (!token.isNullOrEmpty()) {
//                 setBioToken(token)
//             }
//         }
//     }
//
//     private suspend fun setHasPromptedBiometrics(prompted: Boolean = true) {
//         flowSettings.putBoolean(HAS_PROMPTED_FOR_BIOMETRICS, prompted)
//     }
//
//     val hasPromptedForBiometrics: Flow<Boolean> = flowSettings.getBooleanFlow(
//         HAS_PROMPTED_FOR_BIOMETRICS,
//         false
//     )
//
//     fun isBioEnabled(): Flow<Boolean> = flowSettings.getBooleanFlow(IS_BIO_ENABLED, false)
//     private suspend fun getBioToken() = flowSettings.getStringFlow(BIO_TOKEN, "").first()
//
//     val bioToken = flowSettings.getStringOrNullFlow(BIO_TOKEN)
//     suspend fun bioToken() {
//         val encryptedToken = getBioToken()
//         if (encryptedToken.isNotBlank()) {
//             val decryptedToken = encryptedToken.decrypt()
//             if (decryptedToken.isNotEmpty()) {
//                 appSettings.setToken(decryptedToken)
//             }
//         }
//     }
//
//     private suspend fun disableBio() {
//         setBioEnabled(false)
//         setHasPromptedBiometrics(false)
//     }
//
//     private suspend fun setBioToken(token: String) {
//         if (isBioEnabled().first()) {
//             flowSettings.putString(BIO_TOKEN, token.encrypt())
//         }
//     }
//
//     private suspend fun clearBioToken() {
//         flowSettings.putString(BIO_TOKEN, "")
//     }
//
//     suspend fun handleUnsuccessfulBioLogin() {
//         disableBio()
//     }
//
//     suspend fun clear() {
//         flowSettings.clear()
//     }
//
//     fun cryptoObject(): BiometricPrompt.CryptoObject = encryptionManager.getCryptoObject()
//
//     companion object {
//         private const val BIO_TOKEN = "BIO_TOKEN"
//         private const val IS_BIO_ENABLED = "IS_BIO_ENABLED"
//         private const val HAS_PROMPTED_FOR_BIOMETRICS = "HAS_PROMPTED_FOR_BIOMETRICS"
//     }
// }
