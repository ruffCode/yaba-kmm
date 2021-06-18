package tech.alexib.yaba.kmm.di

import co.touchlab.kermit.Kermit
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module
import tech.alexib.yaba.kmm.AppInfo
import tech.alexib.yaba.kmm.data.api.apiModule
import tech.alexib.yaba.kmm.data.repository.AuthRepository
import tech.alexib.yaba.kmm.data.repository.AuthRepositoryImpl

fun initKoin(appModule: Module): KoinApplication {

    val koinApplication = startKoin {
        modules(
            appModule,
            apiModule,
            repositoryModule,
            platformModule,
//            coreModule
        )
    }

    // Dummy initialization logic, making use of appModule declarations for demonstration purposes.
    val koin = koinApplication.koin
    val doOnStartup =  koin.get<() -> Unit>() // doOnStartup is a lambda which is implemented in Swift on iOS side
    doOnStartup.invoke()

    val kermit = koin.get<Kermit> { parametersOf(null) }
    val appInfo = koin.get<AppInfo>() // AppInfo is a Kotlin interface with separate Android and iOS implementations
    kermit.v { "App Id ${appInfo.appId}" }

    return koinApplication
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), getWith("AuthRepository")) }
}

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module