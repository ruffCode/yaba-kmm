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
package tech.alexib.yaba.android.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.biometrics.BiometricsManager
import tech.alexib.yaba.data.repository.AuthRepository
import tech.alexib.yaba.model.response.AuthResult
import tech.alexib.yaba.util.stateInDefault

class LoginScreenViewModel(
    private val authRepository: AuthRepository,
    private val biometricsManager: BiometricsManager
) : ViewModel(), KoinComponent {

    private val isBioEnabledFlow = MutableStateFlow(false)
    private val hasCancelledBiometricLogin = MutableStateFlow(false)
    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")
    private val errorMessage = MutableStateFlow<String?>(null)
    private val loggedIn = MutableStateFlow(false)

    private val shouldPromptForBiometrics: Flow<Boolean> =
        combine(isBioEnabledFlow, hasCancelledBiometricLogin) { isEnabled, hasCancelled ->
            isEnabled && !hasCancelled
        }

    val state: StateFlow<LoginScreenState> =
        combine(
            email,
            password,
            errorMessage,
            loggedIn,
            shouldPromptForBiometrics
        ) { email, password, error, loggedIn, shouldPromptForBiometrics ->

            LoginScreenState(
                email = email,
                password = password,
                errorMessage = error,
                loggedIn = loggedIn,
                isBiometricAuthEnabled = isBioEnabledFlow.value,
                shouldPromptForBiometrics = shouldPromptForBiometrics,
                shouldSetupBiometrics = biometricsManager.shouldPromptSetupBiometrics().first()
            )
        }.stateInDefault(viewModelScope, LoginScreenState.Empty)

    fun login() {
        if (credentialsAreValid()) {
            viewModelScope.launch {
                val result = authRepository.login(email.value, password.value)
                handleAuthResult(result)
            }
        }
    }

    private fun handleAuthResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> loggedIn.value = true
            is AuthResult.Error -> errorMessage.value = result.message
        }
    }

    init {

        viewModelScope.launch {
            biometricsManager.observeIsBiometricsEnabled().collect {
                isBioEnabledFlow.emit(it)
            }
        }
    }

    fun loginBio() {
        viewModelScope.launch {
            biometricsManager.promptForBiometrics().first().let {
                biometricsManager.handleBiometricAuthResult(
                    it,
                    onSuccess = {
                        handleAuthResult(biometricsManager.handleBioLogin())
                    },
                    onError = {
                        errorMessage.value = "Authentication Failed"
                    },
                    onCancel = {
                        hasCancelledBiometricLogin.value = true
                    }
                )
            }
        }
    }

    fun setEmail(input: String) {
        email.value = input.trim()
    }

    fun setPassword(input: String) {
        password.value = input.trim()
    }

    private fun credentialsAreValid(): Boolean {
        return when {
            !email.value.isValidEmail() -> {
                errorMessage.value = "Invalid email"
                false
            }
            password.value.length < 12 -> {
                errorMessage.value = "Password must be 12 characters or greater"
                false
            }
            else -> true
        }
    }
}

fun String.isValidEmail(): Boolean = this.isNotEmpty() && this.matches(
    Regex(
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)" +
            "*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    )
)
