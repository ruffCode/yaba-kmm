package tech.alexib.yaba.kmm.android.ui.auth.biometric

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.auth.SessionManagerAndroid

class BiometricSetupScreenViewModel : ViewModel(), KoinComponent {
    private val log: Kermit by inject { parametersOf("BiometricSetupScreenViewModel") }
    private val sessionManager: SessionManagerAndroid by inject()

    private val declined = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val setupSuccessful = MutableStateFlow(false)

    val state: Flow<BiometricSetupScreenState> =
        combine(
            setupSuccessful,
            errorMessage,
            declined
        ) { setupSuccessful, errorMessage, declined ->
            BiometricSetupScreenState(setupSuccessful, errorMessage, declined)
        }

    fun setupBiometrics() {
        viewModelScope.launch {
            sessionManager.setBioEnabled(true)
            sessionManager.promptForBiometrics().first().let {
                sessionManager.handleBiometricAuthResult(
                    it,
                    onSuccess = {
                        setupSuccessful.value = true
                    },
                    onError = {
                        errorMessage.value = "Failed to enroll biometric authentication"
                        declined.value = true
                    },
                    onCancel = {
                        declined.value = true
                    }
                )
            }
        }
    }
}
