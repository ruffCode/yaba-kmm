package tech.alexib.yaba.kmm.android.ui.auth.login

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
import tech.alexib.yaba.kmm.android.ui.auth.register.Email
import tech.alexib.yaba.kmm.android.ui.auth.register.isValid
import tech.alexib.yaba.kmm.auth.SessionManagerAndroid
import tech.alexib.yaba.kmm.data.repository.AuthResult

class LoginScreenViewModel(
    private val sessionManager: SessionManagerAndroid
) : ViewModel(), KoinComponent {

    private val log: Kermit by inject { parametersOf("LoginScreenViewModel") }

    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")
    private val errorMessage = MutableStateFlow<String?>(null)
    private val loggedIn = MutableStateFlow(false)

    val state: Flow<LoginScreenState> =
        combine(email, password, errorMessage, loggedIn) { email, password, error, loggedIn ->

            LoginScreenState(
                email = email,
                password = password,
                errorMessage = error,
                loggedIn = loggedIn
            )
        }


    fun login() {
        if (credentialsAreValid()) {
            viewModelScope.launch {
                val result = sessionManager.login(email.value, password.value)
                log.d { result.toString() }
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

    fun loginBio() {
        viewModelScope.launch {
            handleAuthResult(sessionManager.handleBioLogin())
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
            !Email(email.value).isValid() -> {
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