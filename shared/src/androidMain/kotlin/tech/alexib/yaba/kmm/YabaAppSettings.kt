package tech.alexib.yaba.kmm

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.auth.EncryptionManager
import tech.alexib.yaba.kmm.data.db.AppSettings


internal class BiometricSettings : KoinComponent {

    private val appSettings: AppSettings by inject()

    private val encryptionManager: EncryptionManager by inject()

    private fun String.encrypt(): String = encryptionManager.encrypt(this)
    private fun String.decrypt(): String = encryptionManager.decrypt(this)


    suspend fun bioToken() {
        if (appSettings.isBioEnabled().first()) {
            val encryptedToken = appSettings.getBioToken()
            if (encryptedToken.isNotBlank()) {
                appSettings.setToken(encryptedToken.decrypt())
            }
        }
    }

    suspend fun enableBio() {
        appSettings.setBioEnabled(true)
        appSettings.token().firstOrNull()?.let {
            setBioToken(it)
        }
    }

    suspend fun setBioToken(token: String) {
        appSettings.setBioToken(token.encrypt())
    }


    suspend fun disableBio(){
        appSettings.setBioEnabled(false)
    }

    fun cryptoObject(): BiometricPrompt.CryptoObject? = encryptionManager.getCryptoObject()
}


class YabaAppSettings : AppSettings(), KoinComponent {

    private val appContext: Context by inject()
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yaba-settings")
    private val dataStore: DataStore<Preferences> = appContext.dataStore
    private val log: Kermit by inject { parametersOf("YabaAppSettings") }


    private val dataStoreSettings = DataStoreSettings(dataStore)
    override val flowSettings = dataStoreSettings

    init {
        ensureNeverFrozen()
    }


}