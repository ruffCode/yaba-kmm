package tech.alexib.yaba.kmm

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.auth.EncryptionManager

internal class BiometricSettings : KoinComponent {

    private val appContext: Context by inject()
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yaba-biometric-settings")
    private val dataStore: DataStore<Preferences> = appContext.dataStore
    private val flowSettings: FlowSettings = DataStoreSettings(dataStore)
    private val appSettings: YabaAppSettings by inject()

    private val encryptionManager: EncryptionManager by inject()

    private fun String.encrypt(): String = encryptionManager.encrypt(this)
    private fun String.decrypt(): String = encryptionManager.decrypt(this)

    private suspend fun setBioEnabled(enabled: Boolean) {
        flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
        clearBioToken()
    }

    suspend fun setHasPromptedBiometrics() {
        flowSettings.putBoolean(HAS_PROMPTED_FOR_BIOMETRICS, true)
    }


    val hasPromptedForBiometrics: Flow<Boolean> = flowSettings.getBooleanFlow(
        HAS_PROMPTED_FOR_BIOMETRICS, false
    )

    fun isBioEnabled(): Flow<Boolean> = flowSettings.getBooleanFlow(IS_BIO_ENABLED, false)
    private suspend fun getBioToken() = flowSettings.getStringFlow(BIO_TOKEN, "").first()

    suspend fun bioToken() {
        if (isBioEnabled().first()) {
            val encryptedToken = getBioToken()
            if (encryptedToken.isNotBlank()) {
                appSettings.setToken(encryptedToken.decrypt())
            }
        }
    }

    suspend fun enableBio() {
        setBioEnabled(true)
        appSettings.token().firstOrNull()?.let {
            setBioToken(it)
        }
    }


    private suspend fun disableBio() {
        setBioEnabled(false)
    }

    private suspend fun setBioToken(token: String) {
        if (isBioEnabled().first()) {
            flowSettings.putString(BIO_TOKEN, token.encrypt())
        }
    }

    private suspend fun clearBioToken() {
        flowSettings.putString(BIO_TOKEN, "")
    }

    suspend fun handleUnsuccessfulBioLogin() {
        disableBio()
        appSettings.clearAuthToken()
        appSettings.clearUserId()
    }

    fun cryptoObject(): BiometricPrompt.CryptoObject? = encryptionManager.getCryptoObject()

    companion object {
        private const val BIO_TOKEN = "BIO_TOKEN"
        private const val IS_BIO_ENABLED = "IS_BIO_ENABLED"
        private const val HAS_PROMPTED_FOR_BIOMETRICS = "HAS_PROMPTED_FOR_BIOMETRICS"
    }
}