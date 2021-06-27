package tech.alexib.yaba.kmm.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.repository.AndroidAuthRepository

class SettingsScreenViewModel : ViewModel(), KoinComponent {

    private val authRepository: AndroidAuthRepository by inject()

    fun logout() {
        viewModelScope.launch {
            authRepository.sessionManager.logout()
            delay(300)
        }
    }
}