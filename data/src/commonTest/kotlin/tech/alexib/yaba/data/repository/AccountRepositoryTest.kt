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
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.alexib.yaba.data.db.dao.AccountDao
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import tech.alexib.yaba.data.domain.dto.AccountDto
import tech.alexib.yaba.data.domain.dto.AccountWithTransactionsDto
import tech.alexib.yaba.data.domain.stubs.UserDataStub
import tech.alexib.yaba.data.network.api.AccountApi
import tech.alexib.yaba.util.suspendTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AccountApiMock : AccountApi {
    override suspend fun setHideAccount(hide: Boolean, accountId: Uuid) {
    }

    override fun accountByIdWithTransactions(id: Uuid): Flow<DataResult<AccountWithTransactionsDto>> =
        flow {
            val account = UserDataStub.accounts.firstOrNull { it.id == id }
            val transactions = UserDataStub.transactions.filter { it.accountId == id }
            account?.let {
                emit(
                    Success(
                        AccountWithTransactionsDto(
                            account, transactions
                        )
                    )
                )
            } ?: emit(ErrorResult<AccountWithTransactionsDto>("account not found"))
        }

    override fun accountsByItemId(itemId: Uuid): Flow<DataResult<List<AccountDto>>> = flow {
        val accounts = UserDataStub.accounts.filter { it.itemId == itemId }
        emit(Success(accounts))
    }
}

internal class AccountRepositoryTest : BaseRepositoryTest() {

    private val accountDao = AccountDao.Impl(yabaDb, Dispatchers.Default)
    private val transactionDao = TransactionDao.Impl(yabaDb, Dispatchers.Default)
    private val existingAccountId = uuidFrom("228021f2-7fbc-4929-9c36-01e262c1e858")
    private val accountRepository = AccountRepositoryImpl(
        AccountApiMock(),
        accountDao,
        userIdProvider,
        transactionDao,
        kermit.withTag("AccountRepository")
    )

    @BeforeTest
    fun setup() {
        suspendTest {
            authSettings.setUserId(userId)
            userDao.insertUserData(UserDataStub.userData)
        }
    }

    @Test
    fun getAccountById() = suspendTest {
        accountRepository.getById(existingAccountId).test {
            assertEquals("Plaid Checking", expectItem()?.name)
            expectNoEvents()
        }
    }

    @Test
    fun getAccountByIdFail() = suspendTest {
        accountRepository.getById(uuidFrom("228021f2-7fbc-4929-9c36-01e262c1e859")).test {
            assertEquals(null, expectItem())
            expectNoEvents()
        }
    }
    @Test
    fun setsAccountHidden() = suspendTest {
        accountRepository.getById(existingAccountId).test {
            assertEquals(false, expectItem()?.hidden)
            expectNoEvents()
        }
        transactionDao.selectAllByAccountId(existingAccountId).test {
            assertTrue(expectItem().isNotEmpty())
            expectNoEvents()
        }
        accountRepository.hide(existingAccountId)
        transactionDao.selectAllByAccountId(existingAccountId).test {
            assertTrue(expectItem().isEmpty())
            expectNoEvents()
        }
    }
}
