package tech.alexib.yaba.kmm.data.db

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@ExperimentalSettingsApi
abstract class AppSettings {

    protected abstract val flowSettings: FlowSettings


    private val authTokenFlow = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = authTokenFlow
    suspend fun setAuthToken(token: String) = authTokenFlow.emit(token)


}