package tech.alexib.yaba.kmm.auth

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.BiometricSettings
import tech.alexib.yaba.kmm.data.auth.BiometricsManager
import tech.alexib.yaba.kmm.data.db.AppSettings
import tech.alexib.yaba.kmm.data.repository.AuthApiRepository
import tech.alexib.yaba.kmm.data.repository.AuthResult
import java.util.concurrent.Executor

lateinit var activityForBio: FragmentActivity

class BiometricsManagerAndroid : BiometricsManager, KoinComponent {

    private val coroutineScope: CoroutineScope = MainScope()
    private val log: Kermit by inject { parametersOf("BiometricsManagerAndroid") }
    private val biometricSettings: BiometricSettings by inject()
    private val appSettings: AppSettings by inject()
    private val appContext: Context by inject()
    private val authApiRepository: AuthApiRepository by inject()
    private val ioDispatcher: CoroutineDispatcher by inject()
    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null


    init {
        ensureNeverFrozen()
        if (biometricPrompt == null) {
            coroutineScope.launch {
                if (biometricSettings.isBioEnabled().first() && !appSettings.isLoggedIn().first()) {
                    setupBiometrics()
                }
            }
        }
    }

    override fun shouldPromptSetupBiometrics(): Flow<Boolean> {
        return combine(
            biometricSettings.isBioEnabled(),
            biometricSettings.hasPromptedForBiometrics
        ) { enabled, hasPrompted ->
            !enabled && !hasPrompted
        }
    }

    override fun setupBiometrics(

    ) {
        executor = ContextCompat.getMainExecutor(appContext)
        if (biometricPrompt == null) {
            biometricPrompt =
                BiometricPrompt(
                    activityForBio,
                    executor!!,
                    object : BiometricPrompt.AuthenticationCallback
                        () {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast
                                .makeText(
                                    appContext,
                                    "Authentication error: $errString",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }

                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult,
                        ) {
                            super.onAuthenticationSucceeded(result)
                            coroutineScope.launch {
                                biometricSettings.enableBio()
                                handleBioLogin()
                            }

                            Toast
                                .makeText(
                                    appContext,
                                    "Authentication succeeded! ",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast
                                .makeText(
                                    appContext, "Authentication failed", Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    })
        }
    }


    override fun promptForBiometrics() {
        val cryptoObject = biometricSettings.cryptoObject()
        if (cryptoObject != null) {
            biometricPrompt?.authenticate(
                promptInfo, cryptoObject
            ) ?: setupBiometrics()
        }
    }

    override suspend fun handleBioLogin(): AuthResult {
        val tokenIsValid = withContext(ioDispatcher) {
            biometricSettings.bioToken()
            val tokenVerificationResponse = authApiRepository.verifyToken().get()
            if (tokenVerificationResponse?.id == null) {
                biometricSettings.handleUnsuccessfulBioLogin()
                false
            } else {
                appSettings.setUserId(tokenVerificationResponse.id)
                true
            }
        }
        return withContext(Dispatchers.Main) {
            if (tokenIsValid) AuthResult.Success else AuthResult.Error("Unauthorized")
        }
    }

    private val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo
        .Builder()
        .setTitle("Biometric login for yaba")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Use account email and password")
        .setConfirmationRequired(false)
        .build()
}

