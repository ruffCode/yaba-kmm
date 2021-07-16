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
package tech.alexib.yaba.data.db.dao

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.db.TransactionEntity
import tech.alexib.yaba.data.db.TransactionQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.model.TransactionDetail
import tech.alexib.yaba.model.TransactionType

internal interface TransactionDao {
    suspend fun insert(transaction: TransactionEntity)
    suspend fun insert(transactions: List<TransactionEntity>)
    suspend fun selectAll(userId: Uuid): Flow<List<Transaction>>
    suspend fun selectById(id: Uuid): Flow<TransactionDetail>
    suspend fun selectAllByAccountIdWithDetail(accountId: Uuid): Flow<List<TransactionDetail>>
    fun selectAllByAccountId(accountId: Uuid): Flow<List<Transaction>>
    suspend fun selectByItemId(itemId: Uuid): Flow<List<TransactionDetail>>
    suspend fun deleteByItemId(itemId: Uuid)
    suspend fun deleteByAccountId(accountId: Uuid)
    suspend fun count(userId: Uuid): Flow<Long>
    suspend fun selectRecent(userId: Uuid): Flow<List<Transaction>>
    suspend fun deleteById(id: Uuid)
}

internal class TransactionDaoImpl(
    private val database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher,
) : TransactionDao, KoinComponent {
    private val queries: TransactionQueries = database.transactionQueries

    private val log: Kermit by inject { parametersOf("TransactionDao") }

    init {
        ensureNeverFrozen()
    }

    override suspend fun insert(transactions: List<TransactionEntity>) {
        withContext(backgroundDispatcher) {
            transactions.forEach {
                queries.insert(it)
            }
        }
    }

    override suspend fun insert(transaction: TransactionEntity) {
        withContext(backgroundDispatcher) {
            queries.insert(
                transaction
            )
        }
    }

    override suspend fun selectAll(userId: Uuid): Flow<List<Transaction>> =
        queries.selectAll(userId, transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)

    override suspend fun selectById(id: Uuid): Flow<TransactionDetail> =
        queries.selectById(id, transactionDetailMapper).asFlow().mapToOne()
            .flowOn(backgroundDispatcher)

    override suspend fun selectAllByAccountIdWithDetail(accountId: Uuid):
        Flow<List<TransactionDetail>> {
        return queries.selectAllByAccountIdWithDetail(accountId, transactionDetailMapper).asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)
    }

    override suspend fun selectByItemId(itemId: Uuid): Flow<List<TransactionDetail>> =
        queries.selectByItemId(itemId, transactionDetailMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)

    override suspend fun deleteByItemId(itemId: Uuid) {
        withContext(backgroundDispatcher) {
            queries.deleteByItemId(itemId)
        }
    }

    override fun selectAllByAccountId(accountId: Uuid): Flow<List<Transaction>> =
        queries.selectAllByAccount(accountId, transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)

    override suspend fun selectRecent(userId: Uuid): Flow<List<Transaction>> =
        queries.selectRecent(userId, transactionMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)

    override suspend fun count(userId: Uuid): Flow<Long> =
        queries.count(userId).asFlow().mapToOne().flowOn(backgroundDispatcher)

    override suspend fun deleteByAccountId(accountId: Uuid) {
        withContext(backgroundDispatcher) {
            queries.deleteByAccontId(accountId)
        }
    }

    override suspend fun deleteById(id: Uuid) {
        withContext(backgroundDispatcher) {
            queries.deleteById(id)
        }
    }

    private val transactionMapper = {
        id: Uuid,
        account_id: Uuid,
        _: Uuid,
        _: Uuid?,
        category: String?,
        subcategory: String?,
        type: TransactionType,
        name: String,
        merchant_name: String?,
        date: LocalDate,
        amount: Double,
        iso_currency_code: String?,
        pending: Boolean?,
        ->
        Transaction(
            id = id,
            accountId = account_id,
            name = name,
            type = type,
            amount = amount,
            date = date,
            category = category,
            subcategory = subcategory,
            isoCurrencyCode = iso_currency_code,
            pending = pending ?: false,
            merchantName = merchant_name,
        )
    }

    private val transactionDetailMapper =
        { id: Uuid,
            account_id: Uuid,
            _: Uuid,
            _: Uuid?,
            category: String?,
            subcategory: String?,
            type: TransactionType,
            name: String,
            iso_currency_code: String?,
            date: LocalDate,
            amount: Double,
            pending: Boolean?,
            merchant_name: String?,
            accountName: String?,
            mask: String?,
            institutionName: String?
            ->
            TransactionDetail(
                id = id,
                accountId = account_id,
                name = name,
                type = type,
                amount = amount,
                date = date,
                category = category,
                subcategory = subcategory,
                isoCurrencyCode = iso_currency_code,
                pending = pending,
                merchantName = merchant_name,
                accountName = accountName ?: "Unknown",
                accountMask = mask ?: "0000",
                institutionName = institutionName ?: "unknown"
            )
        }
}
