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

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import tech.alexib.yaba.data.repository.AuthRepository
import tech.alexib.yaba.data.settings.AuthSettings
import tech.alexib.yaba.model.response.AuthResult
import java.util.concurrent.Executor

lateinit var biometricActivity: FragmentActivity

class BiometricsManager(
    private val biometricSettings: BiometricSettings,
    private val log: Kermit,
    private val appContext: Context,
    private val authRepository: AuthRepository,
    private val authSettings: AuthSettings,
) {

    private var executor: Executor? = null

    fun observeIsBiometricsEnabled() = biometricSettings.isBioEnabled()

    private fun canAuthenticate(): Boolean {
        val manager: BiometricManager = BiometricManager.from(appContext)
        return manager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun shouldPromptSetupBiometrics(): Flow<Boolean> {
        return combine(
            biometricSettings.isBioEnabled(),
            biometricSettings.hasPromptedForBiometrics
        ) { enabled, hasPrompted ->
            !enabled && !hasPrompted && canAuthenticate()
        }
    }

    suspend fun setBioEnabled(enabled: Boolean) {
        biometricSettings.setIsBiometricEnabled(enabled)
        if (enabled) {
            setupBiometrics()
        }
    }

    private fun authenticate(): Flow<BiometricAuthResult> =
        callbackFlow {
            setupBiometrics()
            BiometricPrompt(
                biometricActivity,
                executor!!,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        trySend(
                            BiometricAuthResult.Error(
                                errorCode,
                                errString.toString(),
                                errorCode.shouldDisable()
                            )
                        )
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        trySend(BiometricAuthResult.Failed)
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        trySend(BiometricAuthResult.Success)
                    }
                }
            ).run {
                authenticate(promptInfo, EncryptionManager.getCryptoObject())
            }
            awaitClose()
        }.flowOn(Dispatchers.Main)

    fun promptForBiometrics(): Flow<BiometricAuthResult> {
        return authenticate()
    }

    suspend fun handleBiometricAuthResult(
        result: BiometricAuthResult,
        onSuccess: suspend () -> Unit,
        onError: () -> Unit,
        onCancel: () -> Unit
    ) {
        when (result) {
            is BiometricAuthResult.Success -> onSuccess()
            is BiometricAuthResult.Failed -> onError()
            is BiometricAuthResult.Error -> if (result.shouldDisableBiometrics) {
                setBioEnabled(false)
                log.e {
                    """
                            BIOMETRICS ERROR
                            code :${result.errorCode}
                            message: ${result.message}
                    """.trimIndent()
                }
                onError()
            } else {
                onCancel()
            }
        }
    }

    private fun setupBiometrics() {
        if (executor == null) {
            executor = ContextCompat.getMainExecutor(appContext)
        }
    }

    suspend fun handleBioLogin(): AuthResult {
        biometricSettings.decryptAndSetToken()

        val tokenVerificationResponse = authRepository.validateToken().first()
        val tokenIsValid = if (tokenVerificationResponse?.id == null) {
            biometricSettings.disableBiometrics()
            false
        } else {
            authSettings.setUserId(tokenVerificationResponse.id)
            true
        }
        return if (tokenIsValid) AuthResult.Success else AuthResult.Error("Unauthorized")
    }

    private val promptInfo: BiometricPrompt.PromptInfo by lazy {
        BiometricPrompt.PromptInfo
            .Builder()
            .setTitle("Biometric login for yaba")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account email and password")
            .setConfirmationRequired(true)
            .build()
    }

    internal fun Int.shouldDisable() = when (this) {
        BiometricPrompt.ERROR_HW_UNAVAILABLE -> false
        BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> false
        BiometricPrompt.ERROR_TIMEOUT -> false
        BiometricPrompt.ERROR_NO_SPACE -> false
        BiometricPrompt.ERROR_CANCELED -> false
        BiometricPrompt.ERROR_LOCKOUT -> true
        BiometricPrompt.ERROR_VENDOR -> true
        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> true
        BiometricPrompt.ERROR_USER_CANCELED -> false
        BiometricPrompt.ERROR_NO_BIOMETRICS -> true
        BiometricPrompt.ERROR_HW_NOT_PRESENT -> true
        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> false
        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> true
        else -> false
    }
}

sealed class BiometricAuthResult {
    data class Error(
        val errorCode: Int,
        val message: String,
        val shouldDisableBiometrics: Boolean
    ) : BiometricAuthResult()

    object Failed : BiometricAuthResult()
    object Success : BiometricAuthResult()
}
