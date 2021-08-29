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
package tech.alexib.yaba.data.repository

import app.cash.turbine.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import tech.alexib.yaba.data.domain.dto.AccountDto
import tech.alexib.yaba.data.domain.stubs.PlaidItemDtoStubs
import tech.alexib.yaba.data.domain.stubs.UserDataDtoStubs
import tech.alexib.yaba.model.AccountType
import tech.alexib.yaba.util.InvokeStarted
import tech.alexib.yaba.util.InvokeSuccess
import tech.alexib.yaba.util.suspendTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class UserDataRepositoryTest : BaseRepositoryTest() {
    private val userDataRepository = deps.userDataRepository
    private val accountDao = deps.accountDao
    private val accountRepository = deps.accountRepository
    private val itemDao = deps.itemDao
    private val transactionRepository = deps.transactionRepository
    private val userDao = deps.userDao
    private val userRepository = deps.userRepository

    @BeforeTest
    fun setup() = suspendTest {
        login()
    }

    @Test
    fun handleNewItemTest() = suspendTest {
        userDao.insert(user)
        itemDao.count(userId).test {
            assertEquals(0, awaitItem())
            expectNoEvents()
        }
        userDataRepository.handleNewItem(PlaidItemDtoStubs.wellsFargo.id).test {
            assertEquals(InvokeStarted, awaitItem())
            assertEquals(InvokeSuccess, awaitItem())
            awaitComplete()
        }
        itemDao.count(userId).test {
            assertEquals(1, awaitItem())
            expectNoEvents()
        }
        accountDao.selectAll(userId).test {
            val accounts = awaitItem()
            val hidden = accounts.filter { it.hidden }
            assertEquals(4, hidden.size)
            expectNoEvents()
        }
        val balance =
            UserDataDtoStubs.newItemDtoStub.accounts.currentCashBalance()
        accountRepository.currentCashBalance().test {
            assertEquals(balance, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun handleInitialSync() = suspendTest {
        val final =
            UserDataDtoStubs.newItemDtoStub.transactions.size +
                UserDataDtoStubs.userData.transactions.size
        userDataRepository.handleInitialSync().test {
            assertEquals(InvokeStarted, awaitItem())
            assertEquals(InvokeSuccess, awaitItem())
            awaitComplete()
        }
        transactionRepository.getAll().test {
            assertEquals(UserDataDtoStubs.userData.transactions.size, awaitItem().size)
            userDataRepository.handleNewItem(PlaidItemDtoStubs.wellsFargo.id).collect()
            delay(300)
            assertEquals(final, expectMostRecentItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

//    @Test
//    fun initial() = suspendTest {
//
//        val homeStore = HomeStore(
//            deps.observeRecentTransactions,
//            deps.observeCurrentCashBalance,
//            deps.observeUserItemsCount,
//            deps.performInitialSync
//        )
//        homeStore.init(CoroutineScope(deps.backgroundDispatcher))
//        homeStore.state.test {
//            awaitItem()
//            val item = awaitItem()
//            assertTrue(
//                item.recentTransactions.isNotEmpty(),
//                "recent transactions should not be empty"
//            )
//            assertFalse(item.loading, "Loading two should be false")
//            cancelAndIgnoreRemainingEvents()
//        }
//    }

    @AfterTest
    fun breakdown() = suspendTest {
        cleanup()
    }
}

fun List<AccountDto>.currentCashBalance() = filter {
    !it.hidden &&
        it.type == AccountType.DEPOSITORY
}.sumOf { it.currentBalance }
