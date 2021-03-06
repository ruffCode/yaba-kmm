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
package tech.alexib.yaba.data

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Kermit
import com.russhwolf.settings.MockSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import tech.alexib.yaba.data.db.YabaDatabase
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.data.db.dao.AccountDao
import tech.alexib.yaba.data.db.dao.InstitutionDao
import tech.alexib.yaba.data.db.dao.ItemDao
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.db.dao.UserDao
import tech.alexib.yaba.data.interactor.AddItem
import tech.alexib.yaba.data.interactor.PerformInitialSync
import tech.alexib.yaba.data.mock.api.AccountApiMock
import tech.alexib.yaba.data.mock.api.AuthApiMock
import tech.alexib.yaba.data.mock.api.PlaidItemApiMock
import tech.alexib.yaba.data.mock.api.PushTokenApiMock
import tech.alexib.yaba.data.mock.api.UserDataApiMock
import tech.alexib.yaba.data.network.api.AccountApi
import tech.alexib.yaba.data.network.api.AuthApi
import tech.alexib.yaba.data.network.api.PlaidItemApi
import tech.alexib.yaba.data.network.api.PushTokenApi
import tech.alexib.yaba.data.network.api.UserDataApi
import tech.alexib.yaba.data.observer.ObserveCurrentCashBalance
import tech.alexib.yaba.data.observer.ObserveRecentTransactions
import tech.alexib.yaba.data.observer.ObserveUserItemsCount
import tech.alexib.yaba.data.provider.UserIdProvider
import tech.alexib.yaba.data.repository.AccountRepository
import tech.alexib.yaba.data.repository.AccountRepositoryImpl
import tech.alexib.yaba.data.repository.AuthRepository
import tech.alexib.yaba.data.repository.ItemRepository
import tech.alexib.yaba.data.repository.ItemRepositoryImpl
import tech.alexib.yaba.data.repository.TransactionRepository
import tech.alexib.yaba.data.repository.TransactionRepositoryImpl
import tech.alexib.yaba.data.repository.UserRepository
import tech.alexib.yaba.data.repository.UserRepositoryImpl
import tech.alexib.yaba.data.settings.AuthSettings

internal object TestDependencies {

    val kermit = Kermit(CommonLogger())
    val backgroundDispatcher = Dispatchers.Unconfined
    val settings = MockSettings().toFlowSettings(backgroundDispatcher)
    val authSettings = AuthSettings.Impl(settings)
    val userIdProvider: UserIdProvider by lazy {
        UserIdProvider.Impl(
            authSettings, backgroundDispatcher,
            kermit.withTag("UserIdProvider")
        )
    }

    private val driver: SqlDriver = createInMemorySqlDriver()
    val database: YabaDatabase by lazy {
        YabaDatabase(driver, kermit.withTag("YabaDatabase"))
    }
    val yabaDb: YabaDb by lazy {
        database.getInstance()
    }

    val userDao: UserDao by lazy { UserDao.Impl(yabaDb, backgroundDispatcher) }

    val itemDao: ItemDao by lazy { ItemDao.Impl(yabaDb, backgroundDispatcher) }

    val accountDao: AccountDao by lazy { AccountDao.Impl(yabaDb, backgroundDispatcher) }

    val transactionDao: TransactionDao by lazy { TransactionDao.Impl(yabaDb, backgroundDispatcher) }

    val institutionDao: InstitutionDao by lazy { InstitutionDao.Impl(yabaDb, backgroundDispatcher) }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userIdProvider, kermit.withTag("UserRepository"), userDao)
    }

    val accountRepository: AccountRepository by lazy {
        AccountRepositoryImpl(
            accountApi,
            accountDao,
            userIdProvider,
            transactionDao,
            kermit.withTag("AccountRepository")
        )
    }
    val authRepository: AuthRepository by lazy {
        AuthRepository.Impl(authApi, authSettings, kermit, userRepository)
    }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(
            plaidItemApi = plaidItemApi,
            itemDao = itemDao,
            accountDao = accountDao,
            userIdProvider = userIdProvider,
            institutionDao = institutionDao,
            transactionDao = transactionDao
        )
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(userIdProvider, transactionDao)
    }
    val accountApi: AccountApi by lazy { AccountApiMock() }

    val authApi: AuthApi by lazy { AuthApiMock() }

    val plaidItemApi: PlaidItemApi by lazy { PlaidItemApiMock() }

    val userDataApi: UserDataApi by lazy { UserDataApiMock() }

    val pushTokenApi: PushTokenApi by lazy { PushTokenApiMock() }

    val observeUserItemsCount: ObserveUserItemsCount by lazy {
        ObserveUserItemsCount(
            itemRepository
        )
    }
    val observeRecentTransactions: ObserveRecentTransactions by lazy {
        ObserveRecentTransactions(transactionRepository)
    }

    val observeCurrentCashBalance: ObserveCurrentCashBalance by lazy {
        ObserveCurrentCashBalance(accountRepository)
    }
    val performInitialSync: PerformInitialSync by lazy {
        PerformInitialSync(
            userDataApi,
            userDao,
            kermit,
            itemRepository,
            backgroundDispatcher
        )
    }

    val addItem: AddItem by lazy {
        AddItem(
            log = kermit.withTag("AddItem"),
            backgroundDispatcher = backgroundDispatcher,
            plaidItemApi = plaidItemApi,
            itemRepository = itemRepository
        )
    }
}
