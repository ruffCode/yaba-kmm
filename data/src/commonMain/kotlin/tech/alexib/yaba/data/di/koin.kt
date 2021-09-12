/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.alexib.yaba.data.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import tech.alexib.yaba.data.db.di.dbModule
import tech.alexib.yaba.data.db.di.dbPlatformModule
import tech.alexib.yaba.data.domain.AuthTokenProvider
import tech.alexib.yaba.data.interactor.interactorModule
import tech.alexib.yaba.data.network.di.apiModule
import tech.alexib.yaba.data.observer.observersModule
import tech.alexib.yaba.data.provider.AuthTokenProviderImpl
import tech.alexib.yaba.data.provider.UserIdProvider
import tech.alexib.yaba.data.repository.AccountRepository
import tech.alexib.yaba.data.repository.AccountRepositoryImpl
import tech.alexib.yaba.data.repository.AuthRepository
import tech.alexib.yaba.data.repository.ItemRepository
import tech.alexib.yaba.data.repository.ItemRepositoryImpl
import tech.alexib.yaba.data.repository.PushTokenRepository
import tech.alexib.yaba.data.repository.TransactionRepository
import tech.alexib.yaba.data.repository.TransactionRepositoryImpl
import tech.alexib.yaba.data.repository.UserRepository
import tech.alexib.yaba.data.repository.UserRepositoryImpl
import tech.alexib.yaba.data.store.storeModule
import tech.alexib.yaba.di.CoreDependencies.ioDispatcherQualifier
import tech.alexib.yaba.util.getWith

internal val repositoryModule = module {

    single<AuthRepository> { AuthRepository.Impl(get(), get(), getWith("AuthRepository"), get()) }
    single<AuthTokenProvider> { AuthTokenProviderImpl(get()) }
    single<UserIdProvider> {
        UserIdProvider.Impl(
            get(),
            get(ioDispatcherQualifier),
            getWith("UserIdProvider")
        )
    }
    single<AccountRepository> {
        AccountRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            getWith("AccountRepository")
        )
    }
    single<ItemRepository> { ItemRepositoryImpl(get(), get(), get(), get(), get(), get()) }
    single<PushTokenRepository> { PushTokenRepository.Impl(get(), get(ioDispatcherQualifier)) }
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), getWith("UserRepository"), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        apiModule, dbModule, dbPlatformModule, repositoryModule,
        interactorModule, observersModule, platformModule, storeModule
    )
}

expect val platformModule: Module
