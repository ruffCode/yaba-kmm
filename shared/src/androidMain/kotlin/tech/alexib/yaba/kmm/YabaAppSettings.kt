package tech.alexib.yaba.kmm

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.russhwolf.settings.datastore.DataStoreSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.kmm.data.db.AppSettings


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