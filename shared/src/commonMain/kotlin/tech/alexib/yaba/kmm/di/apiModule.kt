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
