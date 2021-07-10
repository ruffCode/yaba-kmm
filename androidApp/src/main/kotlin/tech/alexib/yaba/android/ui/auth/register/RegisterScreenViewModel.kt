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
package tech.alexib.yaba.android.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.android.ui.auth.login.isValidEmail
import tech.alexib.yaba.data.auth.SessionManagerAndroid
import tech.alexib.yaba.data.repository.AuthResult

class RegisterScreenViewModel : ViewModel(), KoinComponent {

    private val sessionManager: SessionManagerAndroid by inject()

    private val log: Kermit by inject { parametersOf("RegisterUserViewModel") }

    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")
    private val errorMessage = MutableStateFlow<String?>(null)
    private val registrationSuccess = MutableStateFlow(false)
    val state: Flow<RegistrationScreenState> = combine(
        email,
        password,
        errorMessage,
        registrationSuccess
    ) { e, p, errorMessage, registrationSuccess ->
        RegistrationScreenState(
            email = e,
            password = p,
            errorMessage = errorMessage,
            registrationSuccess = registrationSuccess
        )
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

    fun register() {
        viewModelScope.launch {
            if (credentialsAreValid()) {
                when (val result = sessionManager.register(email.value, password.value)) {
                    AuthResult.Success -> registrationSuccess.value = true
                    is AuthResult.Error -> {
                        log.e { "registration error ${result.message}" }
                        registrationSuccess.value = false
                        errorMessage.value = result.message
                    }
                }
            }
        }
    }

    fun setEmail(input: String) {
        email.value = input.trim()
    }

    fun setPassword(input: String) {
        password.value = input.trim()
    }
}
