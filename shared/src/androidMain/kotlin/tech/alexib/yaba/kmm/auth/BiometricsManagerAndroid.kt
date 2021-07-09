package tech.alexib.yaba.kmm.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_CANCELED
import androidx.biometric.BiometricPrompt.ERROR_HW_NOT_PRESENT
import androidx.biometric.BiometricPrompt.ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricPrompt.ERROR_LOCKOUT
import androidx.biometric.BiometricPrompt.ERROR_LOCKOUT_PERMANENT
import androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON
import androidx.biometric.BiometricPrompt.ERROR_NO_BIOMETRICS
import androidx.biometric.BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt.ERROR_NO_SPACE
import androidx.biometric.BiometricPrompt.ERROR_TIMEOUT
import androidx.biometric.BiometricPrompt.ERROR_UNABLE_TO_PROCESS
import androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED
import androidx.biometric.BiometricPrompt.ERROR_VENDOR
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.BiometricSettings
import tech.alexib.yaba.kmm.data.db.AppSettings
import tech.alexib.yaba.kmm.data.repository.AuthApiRepository
import tech.alexib.yaba.kmm.data.repository.AuthResult
import java.util.concurrent.Executor

lateinit var activityForBio: FragmentActivity

class BiometricsManagerAndroid : BiometricsManager, KoinComponent {

    private val log: Kermit by inject { parametersOf("BiometricsManagerAndroid") }
    private val biometricSettings: BiometricSettings by inject()
    private val appSettings: AppSettings by inject()
    private val appContext: Context by inject()
    private val authApiRepository: AuthApiRepository by inject()
    private var executor: Executor? = null

    override val bioToken: Flow<String?> = biometricSettings.bioToken

    init {
        ensureNeverFrozen()
    }

    override val isBioEnabled: Flow<Boolean> = biometricSettings.isBioEnabled()

    fun canAuthenticate(): Boolean {
        val manager: BiometricManager = BiometricManager.from(appContext)
        return manager.canAuthenticate(BIOMETRIC_STRONG) == BIOMETRIC_SUCCESS
    }

    override suspend fun clear() {
        biometricSettings.clear()
    }

    override fun shouldPromptSetupBiometrics(): Flow<Boolean> {
        return combine(
            biometricSettings.isBioEnabled(),
            biometricSettings.hasPromptedForBiometrics
        ) { enabled, hasPrompted ->
            !enabled && !hasPrompted && canAuthenticate()
        }
    }

    override suspend fun setBioEnabled(enabled: Boolean) {
        biometricSettings.setBioEnabled(enabled)
        if (enabled) {
            setupBiometrics()
        }
    }

    private fun setupBiometrics() {
        if (executor == null) {
            executor = ContextCompat.getMainExecutor(appContext)
        }
    }

    private fun authenticate(): Flow<BiometricAuthResult> =
        callbackFlow<BiometricAuthResult> {
            setupBiometrics()
            BiometricPrompt(
                activityForBio,
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
                authenticate(promptInfo, biometricSettings.cryptoObject())
            }
            awaitClose()
        }.flowOn(Dispatchers.Main)

    override fun promptForBiometrics(): Flow<BiometricAuthResult> {
        return authenticate()
    }

    override suspend fun handleBiometricAuthResult(
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
                            BIO ERROR
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

    override suspend fun handleBioLogin(): AuthResult {
        biometricSettings.bioToken()

        val tokenVerificationResponse = authApiRepository.verifyToken().get()
        val tokenIsValid = if (tokenVerificationResponse?.id == null) {
            biometricSettings.handleUnsuccessfulBioLogin()
            false
        } else {
            appSettings.setUserId(tokenVerificationResponse.id)
            true
        }
        return if (tokenIsValid) AuthResult.Success else AuthResult.Error("Unauthorized")
    }

    private val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo
        .Builder()
        .setTitle("Biometric login for yaba")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Use account email and password")
        .setConfirmationRequired(true)
        .build()

    internal fun Int.shouldDisable() = when (this) {
        ERROR_HW_UNAVAILABLE -> false
        ERROR_UNABLE_TO_PROCESS -> false
        ERROR_TIMEOUT -> false
        ERROR_NO_SPACE -> false
        ERROR_CANCELED -> false
        ERROR_LOCKOUT -> true
        ERROR_VENDOR -> true
        ERROR_LOCKOUT_PERMANENT -> true
        ERROR_USER_CANCELED -> false
        ERROR_NO_BIOMETRICS -> true
        ERROR_HW_NOT_PRESENT -> true
        ERROR_NEGATIVE_BUTTON -> false
        ERROR_NO_DEVICE_CREDENTIAL -> true
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
