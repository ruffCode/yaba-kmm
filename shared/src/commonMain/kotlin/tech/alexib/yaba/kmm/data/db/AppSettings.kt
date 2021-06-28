package tech.alexib.yaba.kmm.data.db

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


@ExperimentalSettingsApi
abstract class AppSettings {

    protected abstract val flowSettings: FlowSettings

    fun userId(): Flow<Uuid?> = flowSettings.getStringOrNullFlow(USER_ID).map { userId ->
        if (!userId.isNullOrEmpty()) {
            uuidFrom(userId)
        } else null
    }


    fun showOnboarding(): Flow<Boolean> = flowSettings.getBooleanFlow(SHOW_ONBOARDING, true)

    fun shouldPromptForBiometrics() {

    }

    fun token(): Flow<String?> = flowSettings.getStringOrNullFlow(AUTH_TOKEN)


    private val authTokenFlow = MutableStateFlow<String?>(null)

    val authToken: StateFlow<String?>
        get() = authTokenFlow

    suspend fun clearAuthToken() {
        flowSettings.putString(AUTH_TOKEN, "")
        authTokenFlow.emit(null)
    }

    suspend fun setShowOnboarding(show: Boolean) {
        flowSettings.putBoolean(SHOW_ONBOARDING, show)
    }

    suspend fun clearAppSettings() {
        flowSettings.clear()
    }

    suspend fun setUserId(userId: Uuid) {
        flowSettings.putString(USER_ID, userId.toString())
    }

    suspend fun getBioToken() = flowSettings.getStringFlow(BIO_TOKEN, "").first()

    suspend fun setToken(token: String) {
        flowSettings.putString(AUTH_TOKEN, token)
        authTokenFlow.emit(token)
        setShowOnboarding(false)
    }


    suspend fun setBioToken(token: String) {
        flowSettings.putString(BIO_TOKEN, token)
    }

    suspend fun clearBioToken() {
        flowSettings.putString(BIO_TOKEN, "")
    }

    suspend fun clearUserId() {
        flowSettings.putString(USER_ID, "")
    }

    suspend fun setBioEnabled(enabled: Boolean) {
        flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
        clearBioToken()
    }

    private suspend fun setHasPromptedBiometrics() {
        flowSettings.putBoolean(HAS_PROMPTED_FOR_BIOMETRICS, true)
    }

    fun isBioEnabled(): Flow<Boolean> = flowSettings.getBooleanFlow(IS_BIO_ENABLED, false)

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val USER_ID = "USER_ID"
        private const val SHOW_ONBOARDING = "SHOW_ONBOARDING"
        private const val TEST_TOKEN = "TEST_TOKEN"
        private const val BIO_TOKEN = "BIO_TOKEN"
        private const val IS_BIO_ENABLED = "IS_BIO_ENABLED"
        private const val HAS_PROMPTED_FOR_BIOMETRICS = "HAS_PROMPTED_FOR_BIOMETRICS"
    }

}