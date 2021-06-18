package tech.alexib.yaba.kmm.data.api

import org.koin.dsl.module
import tech.alexib.yaba.kmm.di.getWith

val apiModule = module {
    single {
        ApolloApi("https://ruffrevival.ngrok.io/graphql", getWith("ApolloAPi"))
    }
    single<AuthApi> {
        AuthApiImpl(get())
    }
}
