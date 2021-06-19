package tech.alexib.yaba.kmm.di

import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.alexib.yaba.kmm.YabaAppSettings
import tech.alexib.yaba.kmm.auth.CipherWrapper
import tech.alexib.yaba.kmm.auth.EncryptionManager
import tech.alexib.yaba.kmm.auth.SessionManagerImpl
import tech.alexib.yaba.kmm.data.auth.SessionManager
import tech.alexib.yaba.kmm.data.repository.AndroidAuthRepository

actual val platformModule: Module = module {
    single { createIoDispatcher() }
    single { EncryptionManager(getWith("EncryptionManager")) }
    single { CipherWrapper() }
    single<SessionManager> { SessionManagerImpl(get()) }
    single { AndroidAuthRepository(getWith("AndroidAuthRepository"), get(), get(), get()) }
    single { YabaAppSettings(get()) }
    val baseKermit = Kermit(LogcatLogger()).withTag("Yaba")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }

}

internal fun createIoDispatcher() = Dispatchers.Default