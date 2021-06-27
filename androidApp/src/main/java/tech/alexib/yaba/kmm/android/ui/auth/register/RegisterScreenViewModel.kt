package tech.alexib.yaba.kmm.android.ui.auth.register

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
import tech.alexib.yaba.kmm.data.repository.AndroidAuthRepository
import tech.alexib.yaba.kmm.data.repository.AuthResult

class RegisterScreenViewModel : ViewModel(), KoinComponent {

    private val authRepository: AndroidAuthRepository by inject()

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

    fun register() {
        viewModelScope.launch {

            if (credentialsAreValid()) {

                when (val result = authRepository.register(email.value, password.value)) {
                    AuthResult.Success -> {
                        log.d { "registration success" }
                        registrationSuccess.value = true
                    }
                    is AuthResult.Error -> {
                        log.d { "registration error ${result.message}" }
                        registrationSuccess.value = false
                        errorMessage.value = result.message
                    }

                }
            }

        }
    }

    fun setEmail(input: String) {
        email.value = input
    }

    fun setPassword(input: String) {
        password.value = input
    }

}


@JvmInline
value class Email(val value: String)

fun Email.isValid(): Boolean = this.value.isNotEmpty() && this.value.matches(
    Regex("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
)
