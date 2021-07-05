package tech.alexib.yaba.kmm.di

import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.alexib.yaba.kmm.BiometricSettings
import tech.alexib.yaba.kmm.YabaAppSettings
import tech.alexib.yaba.kmm.auth.BiometricsManager
import tech.alexib.yaba.kmm.auth.BiometricsManagerAndroid
import tech.alexib.yaba.kmm.auth.CipherWrapper
import tech.alexib.yaba.kmm.auth.SessionManagerAndroid
import tech.alexib.yaba.kmm.data.db.AppSettings
import tech.alexib.yaba.kmm.data.db.DriverFactory


actual val platformModule: Module = module {
    single { createIoDispatcher() }
    single { CipherWrapper() }
    single { SessionManagerAndroid(get(), get()) }
    single<BiometricsManager> { BiometricsManagerAndroid() }
    single<AppSettings> { YabaAppSettings() }
    single { BiometricSettings() }
    single<SqlDriver> { DriverFactory(get(), getWith("SqlDelight")).createDriver() }
    val baseKermit = Kermit(LogcatLogger()).withTag("Yaba")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }

}

internal fun createIoDispatcher() = Dispatchers.Default