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
package tech.alexib.yaba.kmm.di

import org.koin.dsl.module
import tech.alexib.yaba.kmm.data.Initializer
import tech.alexib.yaba.kmm.data.InitializerImpl
import tech.alexib.yaba.kmm.data.repository.AccountRepository
import tech.alexib.yaba.kmm.data.repository.AccountRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.AuthApiRepository
import tech.alexib.yaba.kmm.data.repository.AuthApiRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.ItemRepository
import tech.alexib.yaba.kmm.data.repository.ItemRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.TransactionRepository
import tech.alexib.yaba.kmm.data.repository.TransactionRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.UserIdProvider
import tech.alexib.yaba.kmm.data.repository.UserRepository
import tech.alexib.yaba.kmm.data.repository.UserRepositoryImpl

val repositoryModule = module {
    single { UserIdProvider() }
    single<AuthApiRepository> { AuthApiRepositoryImpl(get(), getWith("AuthRepository")) }
    single<Initializer> { InitializerImpl() }
    single<AccountRepository> { AccountRepositoryImpl() }
    single<TransactionRepository> { TransactionRepositoryImpl() }
    single<ItemRepository> { ItemRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl(get()) }
}
