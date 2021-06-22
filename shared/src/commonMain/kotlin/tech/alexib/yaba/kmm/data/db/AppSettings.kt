package tech.alexib.yaba.kmm.data.db

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


@ExperimentalSettingsApi
abstract class AppSettings {

    protected abstract val flowSettings: FlowSettings


    val authToken: Flow<String?> by lazy {
        flowSettings.getStringOrNullFlow(AUTH_TOKEN)
    }
    val showOnBoarding: Flow<Boolean> by lazy {
        flowSettings.getBooleanFlow(SHOW_ONBOARDING, true)
    }

    fun token(): Flow<String?> = flowSettings.getStringOrNullFlow("AUTH_TOKEN")

    suspend fun tokenSync(): String? = token().firstOrNull()

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

    suspend fun getBioToken() = flowSettings.getStringFlow(BIO_TOKEN, "").first()

    suspend fun setToken(token: String) {
        setAuthToken(token)
//        if (isBioEnabled().first()) {
//            setBioToken(token)
//        }
    }


    suspend fun setBioToken(token: String) {
        flowSettings.putString(BIO_TOKEN, token)
    }

    suspend fun clearBioToken() {
        flowSettings.putString(BIO_TOKEN, "")
    }

    suspend fun setBioEnabled(enabled: Boolean) {
        flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
        clearBioToken()
    }

    fun isBioEnabled(): Flow<Boolean> = flowSettings.getBooleanFlow(IS_BIO_ENABLED, false)

    companion object {
        const val AUTH_TOKEN = "AUTH_TOKEN"
        const val SHOW_ONBOARDING = "SHOW_ONBOARDING"
        private const val TEST_TOKEN = "TEST_TOKEN"
        private const val BIO_TOKEN = "BIO_TOKEN"
        private const val IS_BIO_ENABLED = "IS_BIO_ENABLED"
    }

}