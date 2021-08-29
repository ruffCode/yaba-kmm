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
package tech.alexib.yaba.data.biometrics

import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import tech.alexib.yaba.data.settings.AuthSettings

interface BiometricSettings {
    suspend fun setIsBiometricEnabled(enabled: Boolean)
    val hasPromptedForBiometrics: Flow<Boolean>
    fun isBioEnabled(): Flow<Boolean>
    val biometricToken: Flow<String?>
    suspend fun decryptAndSetToken()
    suspend fun disableBiometrics()
    suspend fun clearBiometricSettings()
}

internal class BiometricSettingsImpl(
    private val flowSettings: FlowSettings,
    private val authSettings: AuthSettings,
) : BiometricSettings {
    private val encryptionManager = EncryptionManager

    private fun String.encrypt(): String = EncryptionManager.encrypt(this)
    private fun String.decrypt(): String = EncryptionManager.decrypt(this)

    override suspend fun setIsBiometricEnabled(enabled: Boolean) {
        flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
        if (!enabled) {
            clearBiometricToken()
        } else {
            val token = authSettings.token().firstOrNull()
            if (!token.isNullOrEmpty()) {
                setBiometricToken(token)
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

    override val biometricToken = flowSettings.getStringOrNullFlow(BIO_TOKEN)
    override suspend fun decryptAndSetToken() {
        biometricToken.first()?.let { encryptedToken ->
            val decryptedToken = encryptedToken.decrypt()
            if (decryptedToken.isNotEmpty()) {
                authSettings.setToken(decryptedToken)
            }
        }
    }

    override suspend fun disableBiometrics() {
        setIsBiometricEnabled(false)
        setHasPromptedBiometrics(false)
    }

    private suspend fun setBiometricToken(token: String) {
        if (isBioEnabled().first()) {
            flowSettings.putString(BIO_TOKEN, token.encrypt())
        }
    }

    private suspend fun clearBiometricToken() {
        flowSettings.putString(BIO_TOKEN, "")
    }

    override suspend fun clearBiometricSettings() {
        flowSettings.clear()
    }

    companion object {
        private const val BIO_TOKEN = "BIO_TOKEN"
        private const val IS_BIO_ENABLED = "IS_BIO_ENABLED"
        private const val HAS_PROMPTED_FOR_BIOMETRICS = "HAS_PROMPTED_FOR_BIOMETRICS"
    }
}
