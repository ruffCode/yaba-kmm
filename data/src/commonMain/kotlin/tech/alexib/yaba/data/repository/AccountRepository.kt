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

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import tech.alexib.yaba.data.db.dao.AccountDao
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import tech.alexib.yaba.data.network.api.AccountApi
import tech.alexib.yaba.data.provider.UserIdProvider
import tech.alexib.yaba.model.Account

interface AccountRepository {
    fun currentCashBalance(): Flow<Double>
    fun getById(id: Uuid): Flow<Account?>
    suspend fun hide(accountId: Uuid)
    suspend fun show(accountId: Uuid)
    suspend fun updateByItemId(itemId: Uuid)
}

internal class AccountRepositoryImpl(
    private val accountApi: AccountApi,
    private val accountDao: AccountDao,
    private val userIdProvider: UserIdProvider,
    private val transactionDao: TransactionDao,
    private val log: Kermit
) : AccountRepository {
    override fun currentCashBalance(): Flow<Double> = accountDao.currentCashBalance(userIdProvider.userId.value)
//        flow {
// //        emitAll(accountDao.currentCashBalance(userIdProvider.userId.value))
//        accountDao.currentCashBalance(userIdProvider.userId.value)
//    }

    override fun getById(id: Uuid): Flow<Account?> = flow {
        emitAll(accountDao.selectById(id))
    }

    override suspend fun hide(accountId: Uuid) {
        accountApi.setHideAccount(true, accountId)
        accountDao.setHidden(accountId, true)
        transactionDao.deleteByAccountId(accountId)
    }

    override suspend fun show(accountId: Uuid) {
        accountApi.setHideAccount(false, accountId)
        when (val result = accountApi.accountByIdWithTransactions(accountId).firstOrNull()) {
            is Success -> {
                accountDao.setHidden(accountId, false)
                accountDao.insert(result.data.account)
                transactionDao.insert(result.data.transactions)
            }
            is ErrorResult -> log.e { "error retrieving account and transactions ${result.error}" }
            null -> log.e { "result was null $accountId" }
        }
    }

    override suspend fun updateByItemId(itemId: Uuid) {
        when (val result = accountApi.accountsByItemId(itemId).firstOrNull()) {
            is Success -> accountDao.insert(result.data)
            is ErrorResult -> log.e { "Error updating accounts ${result.error}" }
            null -> log.e { "Error updating accounts: result was null" }
        }
    }
}
