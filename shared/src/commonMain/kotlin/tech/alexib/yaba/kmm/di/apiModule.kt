package tech.alexib.yaba.kmm.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import tech.alexib.yaba.kmm.data.api.AccountApi
import tech.alexib.yaba.kmm.data.api.AccountApiImpl
import tech.alexib.yaba.kmm.data.api.ApolloApi
import tech.alexib.yaba.kmm.data.api.AuthApi
import tech.alexib.yaba.kmm.data.api.AuthApiImpl
import tech.alexib.yaba.kmm.data.api.PlaidItemApi
import tech.alexib.yaba.kmm.data.api.PlaidItemApiImpl

internal val apiModule = module {
    single {
        ApolloApi(get(named("serverUrl")), getWith("ApolloAPi"))
    }
    single<AuthApi> {
        AuthApiImpl(get())
    }

    single<PlaidItemApi> { PlaidItemApiImpl() }
    single<AccountApi> { AccountApiImpl() }
}
