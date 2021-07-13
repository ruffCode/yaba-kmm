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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.TransactionsUpdateQuery
import tech.alexib.yaba.data.api.ApolloApi
import tech.alexib.yaba.data.api.ApolloResponse
import tech.alexib.yaba.data.api.dto.toDto
import tech.alexib.yaba.data.api.dto.toEntities
import tech.alexib.yaba.data.api.safeQuery
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.model.TransactionDetail
import tech.alexib.yaba.model.request.UpdateTransactionsRequest

interface TransactionRepository {
    fun recentTransactions(): Flow<List<Transaction>>
    fun count(): Flow<Long>
    fun getAll(): Flow<List<Transaction>>
    fun getById(id: Uuid): Flow<TransactionDetail>
    fun getAllByAccountId(accountId: Uuid): Flow<List<Transaction>>
    suspend fun updateTransactions(updateId: Uuid)
}

internal class TransactionRepositoryImpl : TransactionRepository, KoinComponent {
    private val log: Kermit by inject { parametersOf("TransactionRepository") }
    private val userIdProvider: UserIdProvider by inject()
    private val dao: TransactionDao by inject()
    private val apolloApi: ApolloApi by inject()
    private val accountRepository: AccountRepository by inject()
    private val backgroundDispatcher: CoroutineDispatcher by inject()

    init {
        ensureNeverFrozen()
    }

    override fun recentTransactions(): Flow<List<Transaction>> = flow {
        emitAll(dao.selectRecent(userIdProvider.userId.value))
    }

    override fun count(): Flow<Long> = flow {
        emitAll(dao.count(userIdProvider.userId.value))
    }

    override fun getAll(): Flow<List<Transaction>> =
        flow { emitAll(dao.selectAll(userIdProvider.userId.value)) }

    override fun getById(id: Uuid): Flow<TransactionDetail> = flow {
        emitAll(dao.selectById(id))
    }

    private suspend fun delete(ids: List<Uuid>) {
        ids.forEach {
            dao.deleteById(it)
        }
    }

    override fun getAllByAccountId(accountId: Uuid): Flow<List<Transaction>> = flow {
        emitAll(dao.selectAllByAccountId(accountId))
    }

    private fun getUpdate(updateId: Uuid): Flow<ApolloResponse<UpdateTransactionsRequest?>> =
        apolloApi.client().safeQuery(TransactionsUpdateQuery(updateId)) { data ->
            if (data.transactionsUpdated != null) {
                val added =
                    data.transactionsUpdated.added?.map { it.fragments.transaction.toDto() }
                val removed = data.transactionsUpdated.removed?.map { it as Uuid }
                UpdateTransactionsRequest(added, removed)
            } else null
        }

    override suspend fun updateTransactions(updateId: Uuid) {
        withContext(backgroundDispatcher) {
            when (val update = getUpdate(updateId).firstOrNull()) {
                is ApolloResponse.Success -> {
                    if (update.data != null) {
                        val itemId = update.data.added?.firstOrNull()?.itemId
                        update.data.added?.let {
                            dao.insert(it.toEntities())
                            log.d { "updateTransactions inserted ${it.size}" }
                        }
                        update.data.removed?.let {
                            log.d { "updateTransactions deleted ${it.size}" }
                            delete(it)
                        }
                        itemId?.let {
                            accountRepository.updateByItemId(it)
                            log.d { "updateTransactions updateByItemId $it" }
                        }
                    }
                }
                is ApolloResponse.Error -> log.e { "Error fetching transactions updates: ${update.message}" }
                null -> log.e { "Error fetching transactions updates: response was null" }
            }
        }
    }
}
