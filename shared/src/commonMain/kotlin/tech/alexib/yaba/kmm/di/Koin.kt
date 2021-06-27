package tech.alexib.yaba.kmm.di

import co.touchlab.kermit.Kermit
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module
import tech.alexib.yaba.kmm.AppInfo
import tech.alexib.yaba.kmm.data.Initializer
import tech.alexib.yaba.kmm.data.InitializerImpl
import tech.alexib.yaba.kmm.data.api.apiModule
import tech.alexib.yaba.kmm.data.db.dbModule
import tech.alexib.yaba.kmm.data.repository.AccountRepository
import tech.alexib.yaba.kmm.data.repository.AccountRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.AuthRepository
import tech.alexib.yaba.kmm.data.repository.AuthRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.TransactionRepository
import tech.alexib.yaba.kmm.data.repository.TransactionRepositoryImpl

fun initKoin(appModule: Module): KoinApplication {

    val koinApplication = startKoin {
        modules(
            appModule,
            apiModule,
            repositoryModule,
            platformModule,
            dbModule
        )
    }

    // Dummy initialization logic, making use of appModule declarations for demonstration purposes.
    val koin = koinApplication.koin
    val doOnStartup =
        koin.get<() -> Unit>() // doOnStartup is a lambda which is implemented in Swift on iOS side
    doOnStartup.invoke()

    val kermit = koin.get<Kermit> { parametersOf(null) }
    val appInfo =
        koin.get<AppInfo>() // AppInfo is a Kotlin interface with separate Android and iOS implementations
    kermit.v { "App Id ${appInfo.appId}" }

    return koinApplication
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), getWith("AuthRepository")) }
    single<Initializer> { InitializerImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl() }
    single<TransactionRepository> { TransactionRepositoryImpl() }
}

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module