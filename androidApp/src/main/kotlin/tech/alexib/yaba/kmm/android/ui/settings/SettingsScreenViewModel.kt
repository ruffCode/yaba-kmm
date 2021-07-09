package tech.alexib.yaba.kmm.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.auth.SessionManagerAndroid

class SettingsScreenViewModel : ViewModel(), KoinComponent {

    private val sessionManager: SessionManagerAndroid by inject()

    fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
            delay(300)
        }
    }

    fun clearAppData() {
        viewModelScope.launch {
            sessionManager.clearAppData()
            sessionManager.logout()
            delay(100)
        }
    }
}
