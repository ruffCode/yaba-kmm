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
package tech.alexib.yaba.data.store

import org.koin.core.module.Module
import org.koin.dsl.module
import tech.alexib.yaba.util.getWith

val storeModule: Module = module {
    single { parameters -> HomeStore(get(), get(), get(), get(), parameters.get()) }
    single { AccountDetailStore(get(), get(), get()) }
    single { AccountsStore(get()) }
    single { TransactionDetailStore(get()) }
    single { params -> TransactionsStore(get(), params.get()) }
    single { params ->
        PlaidLinkResultStore(
            get(),
            get(),
            getWith("PlaidLinkResultStore"),
            params.get()
        )
    }
    single { PlaidItemsStore(get()) }
}
