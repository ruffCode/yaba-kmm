package tech.alexib.yaba.kmm

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import co.touchlab.kermit.Kermit
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.auth.EncryptionManager
import tech.alexib.yaba.kmm.data.db.AppSettings

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yaba-settings")
class YabaAppSettings(private val appContext: Context) : AppSettings(), KoinComponent {

    private val encryptionManager: EncryptionManager by inject()


    private val dataStore: DataStore<Preferences> = appContext.dataStore
    private val log: Kermit by inject { parametersOf("YabaAppSettings") }


    private val dataStoreSettings = DataStoreSettings(dataStore)
    override val flowSettings = dataStoreSettings

    fun token(): Flow<String?> = flow {
        flowSettings.getStringFlow("AUTH_TOKEN", "").firstOrNull()?.decrypt()
    }


    suspend fun bioToken() {
        val token: String = flowSettings.getStringFlow(BIO_TOKEN, "").first()
        if (token.isNotEmpty()) {
            setAuthToken(token)
        }
    }

    suspend fun setToken(token: String) {
        log.d { "settings token $token" }
        setAuthToken(token.encrypt())
        if (isBioEnabled().first()) {
            setBioToken(token.encrypt())
        }
    }


    private suspend fun setBioToken(token: String) {
        flowSettings.putString(BIO_TOKEN, token)
    }

    suspend fun clearBioToken() {
        flowSettings.putString(BIO_TOKEN, "")
    }

    suspend fun setBioEnabled(enabled: Boolean) {
        flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
        if (!enabled) {
            clearBioToken()
        } else {
            authToken.first()?.let {
                if (it.isNotBlank()) {
                    setBioToken(it)
                }
            }
        }
    }

    fun cryptoObject(): BiometricPrompt.CryptoObject? = encryptionManager.getCryptoObject()

    private fun String.encrypt(): String = encryptionManager.encrypt(this)
    private fun String.decrypt(): String = encryptionManager.decrypt(this)

    fun isBioEnabled(): Flow<Boolean> = flowSettings.getBooleanFlow(IS_BIO_ENABLED, false)

    companion object {
        private const val TEST_TOKEN = "TEST_TOKEN"
        private const val BIO_TOKEN = "BIO_TOKEN"
        private const val IS_BIO_ENABLED = "IS_BIO_ENABLED"

    }
}