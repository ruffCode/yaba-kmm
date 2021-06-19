package tech.alexib.yaba.kmm.android.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.repository.AndroidAuthRepository
import tech.alexib.yaba.kmm.data.repository.AuthResult

class LoginScreenViewModel(
    private val authRepository: AndroidAuthRepository
) : ViewModel(), KoinComponent {

    private val log: Kermit by inject { parametersOf("LoginScreenViewModel") }

    private val loginResultFlow = MutableStateFlow<AuthResult?>(null)
    val loginResult: StateFlow<AuthResult?> = loginResultFlow

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            log.d { result.toString() }
            loginResultFlow.emit(result)
        }
    }
}