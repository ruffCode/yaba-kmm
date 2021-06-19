package tech.alexib.yaba.kmm.data.db

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

@ExperimentalSettingsApi
abstract class AppSettings {

    protected abstract val flowSettings: FlowSettings


    val authToken: Flow<String?> by lazy {
        flowSettings.getStringOrNullFlow(AUTH_TOKEN)
    }
    val showOnBoarding: Flow<Boolean> by lazy {
        flowSettings.getBooleanFlow(SHOW_ONBOARDING, true)
    }

    suspend fun setAuthToken(token: String) {
        flowSettings.putString(AUTH_TOKEN, token)
        setShowOnBoarding(false)
    }

    suspend fun clearAuthToken() {
        flowSettings.putString(AUTH_TOKEN, "")
    }

    suspend fun setShowOnBoarding(show: Boolean) {
        flowSettings.putBoolean(SHOW_ONBOARDING, show)
    }

    suspend fun clearAppSettings() {
        flowSettings.clear()
    }

    companion object {
        const val AUTH_TOKEN = "AUTH_TOKEN"
        const val SHOW_ONBOARDING = "SHOW_ONBOARDING"
    }

}