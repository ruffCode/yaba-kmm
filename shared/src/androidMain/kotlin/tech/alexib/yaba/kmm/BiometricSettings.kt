package tech.alexib.yaba.kmm

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import co.touchlab.kermit.Kermit
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.auth.EncryptionManager
import tech.alexib.yaba.kmm.data.db.AppSettings

internal class BiometricSettings : KoinComponent {

    private val appContext: Context by inject()
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "yaba-biometric-settings"
    )
    private val dataStore: DataStore<Preferences> = appContext.dataStore
    private val flowSettings: FlowSettings = DataStoreSettings(dataStore)
    private val appSettings: AppSettings by inject()
    private val encryptionManager = EncryptionManager
    private val log: Kermit by inject { parametersOf("BiometricSettings") }
    private fun String.encrypt(): String = encryptionManager.encrypt(this)
    private fun String.decrypt(): String = encryptionManager.decrypt(this)

    suspend fun setBioEnabled(enabled: Boolean) {
        flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
        if (!enabled) {
            clearBioToken()
        } else {
            val token = appSettings.token().firstOrNull()
            if (!token.isNullOrEmpty()) {
                setBioToken(token)
            }
        }
    }

    private suspend fun setHasPromptedBiometrics(prompted: Boolean = true) {
        flowSettings.putBoolean(HAS_PROMPTED_FOR_BIOMETRICS, prompted)
    }

    val hasPromptedForBiometrics: Flow<Boolean> = flowSettings.getBooleanFlow(
        HAS_PROMPTED_FOR_BIOMETRICS,
        false
    )

    fun isBioEnabled(): Flow<Boolean> = flowSettings.getBooleanFlow(IS_BIO_ENABLED, false)
    private suspend fun getBioToken() = flowSettings.getStringFlow(BIO_TOKEN, "").first()

    val bioToken = flowSettings.getStringOrNullFlow(BIO_TOKEN)
    suspend fun bioToken() {
        val encryptedToken = getBioToken()
        if (encryptedToken.isNotBlank()) {
            val decryptedToken = encryptedToken.decrypt()
            if (decryptedToken.isNotEmpty()) {
                appSettings.setToken(decryptedToken)
            }
        }
    }

    private suspend fun disableBio() {
        setBioEnabled(false)
        setHasPromptedBiometrics(false)
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
    }
    suspend fun clear() {
        flowSettings.clear()
    }

    fun cryptoObject(): BiometricPrompt.CryptoObject = encryptionManager.getCryptoObject()

    companion object {
        private const val BIO_TOKEN = "BIO_TOKEN"
        private const val IS_BIO_ENABLED = "IS_BIO_ENABLED"
        private const val HAS_PROMPTED_FOR_BIOMETRICS = "HAS_PROMPTED_FOR_BIOMETRICS"
    }
}
