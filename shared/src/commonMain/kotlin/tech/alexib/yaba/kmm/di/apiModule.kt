package tech.alexib.yaba.kmm.di

import org.koin.dsl.module
import tech.alexib.yaba.kmm.data.api.ApolloApi
import tech.alexib.yaba.kmm.data.api.AuthApi
import tech.alexib.yaba.kmm.data.api.AuthApiImpl
import tech.alexib.yaba.kmm.data.api.PlaidItemApi
import tech.alexib.yaba.kmm.data.api.PlaidItemApiImpl
import kotlin.jvm.JvmInline

@JvmInline
value class ApolloUrl(val value: String)

internal val apiModule = module {
    single {
        ApolloApi(get(), get(), getWith("ApolloAPi"))

    }
    single<AuthApi> {
        AuthApiImpl(get())
    }

    single<PlaidItemApi> { PlaidItemApiImpl(get()) }
}
