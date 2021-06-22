package tech.alexib.yaba.kmm.data.api

import org.koin.dsl.module
import tech.alexib.yaba.kmm.di.getWith
import kotlin.jvm.JvmInline

@JvmInline
value class ApolloUrl(val value: String)

val apiModule = module {
    single {
        ApolloApi(get(), get(), getWith("ApolloAPi"))

    }
    single<AuthApi> {
        AuthApiImpl(get())
    }

    single<PlaidItemApi> { PlaidItemApiImpl(get()) }
}
