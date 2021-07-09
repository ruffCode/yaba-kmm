package tech.alexib.yaba.kmm.android.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.auth.SessionManagerAndroid
import tech.alexib.yaba.kmm.data.repository.AuthResult

class LoginScreenViewModel(
    private val sessionManager: SessionManagerAndroid
) : ViewModel(), KoinComponent {

    private val log: Kermit by inject { parametersOf("LoginScreenViewModel") }

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

    val state: Flow<LoginScreenState> =
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
                shouldSetupBiometrics = sessionManager.shouldPromptSetupBiometrics().first()
            )
        }

    fun login() {
        if (credentialsAreValid()) {
            viewModelScope.launch {
                val result = sessionManager.login(email.value, password.value)
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
            sessionManager.isBioEnabled.collect {
                isBioEnabledFlow.emit(it)
            }
        }
    }

    fun loginBio() {
        viewModelScope.launch {
            sessionManager.promptForBiometrics().first().let {
                sessionManager.handleBiometricAuthResult(
                    it,
                    onSuccess = {
                        handleAuthResult(sessionManager.handleBioLogin())
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
