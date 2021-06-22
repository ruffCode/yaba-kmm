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

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yaba-settings")


class BiometricSettings : KoinComponent {

    private val appSettings: AppSettings by inject<YabaAppSettings>()

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

//    fun token(): Flow<String?> = flowSettings.getStringOrNullFlow("AUTH_TOKEN")
//
//    suspend fun tokenSync(): String? = token().firstOrNull()


//    suspend fun bioToken() {
//        val token: String = flowSettings.getStringFlow(BIO_TOKEN, "").first()
//        if (token.isNotEmpty()) {
//            setAuthToken(token)
//        }
//    }
//
//    suspend fun setToken(token: String) {
//        log.d { "settings token $token" }
//        setAuthToken(token)
//        if (isBioEnabled().first()) {
//            setBioToken(token)
//        }
//    }
//
//
//    private suspend fun setBioToken(token: String) {
//        flowSettings.putString(BIO_TOKEN, token)
//    }
//
//    suspend fun clearBioToken() {
//        flowSettings.putString(BIO_TOKEN, "")
//    }
//
//    suspend fun setBioEnabled(enabled: Boolean) {
//        flowSettings.putBoolean(IS_BIO_ENABLED, enabled)
//        if (!enabled) {
//            clearBioToken()
//        } else {
//            authToken.first()?.let {
//                if (it.isNotBlank()) {
//                    setBioToken(it)
//                }
//            }
//        }
//    }

//    fun cryptoObject(): BiometricPrompt.CryptoObject? = encryptionManager.getCryptoObject()


}