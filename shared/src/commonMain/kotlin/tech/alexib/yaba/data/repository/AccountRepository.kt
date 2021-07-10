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
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.api.AccountApi
import tech.alexib.yaba.data.api.dto.toEntities
import tech.alexib.yaba.data.api.dto.toEntity
import tech.alexib.yaba.data.db.dao.AccountDao
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.model.Account

interface AccountRepository {
    fun getAll(): Flow<List<Account>>
    fun availableCashBalance(): Flow<Double>
    fun currentCashBalance(): Flow<Double>
    suspend fun hide(accountId: Uuid)
    suspend fun show(accountId: Uuid)
}

internal class AccountRepositoryImpl : AccountRepository, KoinComponent {

    private val accountDao: AccountDao by inject()
    private val accountApi: AccountApi by inject()
    private val transactionDao: TransactionDao by inject()
    private val userIdProvider: UserIdProvider by inject()

    private val log: Kermit by inject { parametersOf("AccountRepository") }

    init {
        ensureNeverFrozen()
    }

    override fun getAll(): Flow<List<Account>> = flow {
        emitAll(accountDao.selectAll(userIdProvider.userId.value))
    }

    override fun availableCashBalance(): Flow<Double> = flow {
        emitAll(accountDao.availableBalance(userIdProvider.userId.value))
    }

    override fun currentCashBalance(): Flow<Double> = flow {
        emitAll(accountDao.currentBalance(userIdProvider.userId.value))
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
                accountDao.insert(result.data.account.toEntity())
                transactionDao.insert(result.data.transactions.toEntities())
            }
            is ErrorResult -> log.e { "error retrieving account and transactions ${result.error}" }
            null -> log.e { "result was null $accountId" }
        }
    }
}
