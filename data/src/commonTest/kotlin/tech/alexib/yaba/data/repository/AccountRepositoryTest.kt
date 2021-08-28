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
import com.benasher44.uuid.uuidFrom
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.domain.dto.UserDataDto
import tech.alexib.yaba.data.domain.stubs.UserDataStubs
import tech.alexib.yaba.model.AccountType
import tech.alexib.yaba.util.suspendTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AccountRepositoryTest : BaseRepositoryTest() {

    private val transactionDao: TransactionDao = deps.transactionDao
    private val userDao = deps.userDao
    private val existingAccountId = uuidFrom("228021f2-7fbc-4929-9c36-01e262c1e858")
    private val accountRepository = deps.accountRepository
    private val balance = UserDataStubs.accounts.filter { it.type == AccountType.DEPOSITORY }
        .sumOf { it.currentBalance }
    private val additionalItem: UserDataDto by lazy {
        with(UserDataStubs.newItemDtoStub) {
            UserDataDto(
                user = user,
                accounts = accounts,
                transactions = transactions,
                items = listOf(item),
                institutions = listOf(institutionDto)
            )
        }
    }

    @BeforeTest
    fun setup() {
        suspendTest {
            userDao.deleteById(userId)
            login()
            userDao.insertUserData(UserDataStubs.userData)
        }
    }

    @Test
    fun getAccountById() = suspendTest {
        accountRepository.getById(existingAccountId).test {
            assertEquals("Plaid Checking", awaitItem()?.name)
            expectNoEvents()
        }
    }

    @Test
    fun getAccountByIdFail() = suspendTest {
        accountRepository.getById(uuidFrom("228021f2-7fbc-4929-9c36-01e262c1e859")).test {
            assertEquals(null, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun setsAccountHidden() = suspendTest {
        accountRepository.getById(existingAccountId).test {
            assertEquals(false, awaitItem()?.hidden)
            expectNoEvents()
        }
        transactionDao.selectAllByAccountId(existingAccountId).test {
            assertTrue(awaitItem().isNotEmpty())
            expectNoEvents()
        }
        accountRepository.hide(existingAccountId)
        transactionDao.selectAllByAccountId(existingAccountId).test {
            assertTrue(awaitItem().isEmpty())
            expectNoEvents()
        }
        accountRepository.show(existingAccountId)
        transactionDao.selectAllByAccountId(existingAccountId).test {
            assertTrue(awaitItem().isNotEmpty())
            expectNoEvents()
        }
    }

    @Test
    fun currentCashBalance() = suspendTest {
        accountRepository.currentCashBalance().test {
            assertEquals(balance, awaitItem())
            userDao.insertUserData(additionalItem)
            assertEquals(
                additionalItem.accounts.filter { it.type == AccountType.DEPOSITORY }
                    .sumOf { it.currentBalance } + balance,
                awaitItem()
            )
            expectNoEvents()
        }
    }

    @AfterTest
    fun breakdown() = suspendTest {
        cleanup()
    }
}
