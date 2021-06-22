package tech.alexib.yaba.kmm.data.repository

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.BiometricSettings
import tech.alexib.yaba.kmm.data.auth.SessionManager
import tech.alexib.yaba.kmm.data.db.AppSettings
import tech.alexib.yaba.kmm.model.response.AuthResponse
import java.util.concurrent.Executor

lateinit var activityForBio: FragmentActivity


private val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo
    .Builder()
    .setTitle("Biometric login for YABA")
    .setSubtitle("Log in using your biometric credential")
    .setNegativeButtonText("Use account password")
    .build()

class AndroidAuthRepository(
    log: Kermit,
    val sessionManager: SessionManager,
    private val ioDispatcher: CoroutineDispatcher,

    ) : BiometricsRepository, KoinComponent {
    private val coroutineScope: CoroutineScope = MainScope()
    private val log = log

    private val biometricSettings: BiometricSettings by inject()
    private val appContext: Context by inject()
    private val authRepository: AuthRepository by inject()

    init {
        ensureNeverFrozen()
    }

    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    val bioEnabled: Flow<Boolean> = sessionManager.isBioEnabled()


    suspend fun login(email: String, password: String): AuthResult {
        return runCatching {
            handleAuthResponse(authRepository.login(email, password).getOrThrow())
        }.getOrElse {
            log.e(it) { "Login Error" }
            AuthResult.Error("Authentication Error")
        }

    }

    private suspend fun handleAuthResponse(authResponse: AuthResponse): AuthResult {
        return if (authResponse.token.isNotEmpty()) {
            sessionManager.setToken(authResponse.token)
            AuthResult.Success
        } else AuthResult.Error("Authentication Error")
    }

//    private suspend fun register(input: UserRegisterInput): DataResult<AuthResponse> {
//        return when (val result = authApi.register(input).first()) {
//            is ApolloResponse.Success -> Success(result.data)
//            is ApolloResponse.Error -> {
//                result.errors.forEach {
//                    log.e { it }
//                }
//                ErrorResult("User registration error")
//            }
//        }
//    }

    suspend fun register(email: String, password: String): AuthResult {
        return runCatching {


            handleAuthResponse(authRepository.register(email, password).getOrThrow())


        }.getOrElse {
            AuthResult.Error(it.message ?: "User Registration Error")
        }

    }

    init {
        if (biometricPrompt == null) {
            coroutineScope.launch {
                if (bioEnabled.first() && !sessionManager.isLoggedIn().first()) {
                    setupBiometrics()
                }
            }
        }
    }

    suspend fun handleBioLogin(): AuthResult {
        withContext(ioDispatcher) {
            sessionManager.bioToken()
        }
        return withContext(Dispatchers.Main) {
            AuthResult.Success
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
                            coroutineScope.launch {
                                sessionManager.enableBio()
                                handleBioLogin()
                            }
                            super.onAuthenticationSucceeded(result)
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


}