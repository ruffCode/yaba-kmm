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

import com.benasher44.uuid.Uuid
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import tech.alexib.yaba.data.db.TransactionQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.data.db.mapper.toEntity
import tech.alexib.yaba.data.domain.dto.TransactionDto
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.model.TransactionDetail
import tech.alexib.yaba.model.TransactionType

interface TransactionDao {
    suspend fun insert(transactions: List<TransactionDto>)
    suspend fun deleteByAccountId(accountId: Uuid)
    fun selectRecent(userId: Uuid): Flow<List<Transaction>>
    fun selectAllLikeName(userId: Uuid, query: String? = null): Flow<List<Transaction>>
    fun selectAll(userId: Uuid): Flow<List<Transaction>>
    fun selectById(id: Uuid): Flow<TransactionDetail?>
    fun selectAllByAccountId(accountId: Uuid): Flow<List<Transaction>>
    fun spendingCategoriesByDate(
        userId: Uuid,
        start: LocalDate,
        end: LocalDate
    ): Flow<List<Pair<String, Double>>>

    suspend fun deleteById(id: Uuid)

    class Impl(
        database: YabaDb,
        private val backgroundDispatcher: CoroutineDispatcher,
    ) : TransactionDao {
        private val queries: TransactionQueries = database.transactionQueries

        override suspend fun insert(transactions: List<TransactionDto>) {
            withContext(backgroundDispatcher) {
                transactions.forEach {
                    queries.insert(it.toEntity())
                }
            }
        }

        override suspend fun deleteByAccountId(accountId: Uuid) {
            withContext(backgroundDispatcher) {
                queries.deleteByAccontId(accountId)
            }
        }

        override fun selectRecent(userId: Uuid): Flow<List<Transaction>> =
            queries.selectRecent(userId, transactionMapper).asFlow().mapToList()
                .flowOn(backgroundDispatcher)

        override fun selectAll(userId: Uuid): Flow<List<Transaction>> =
            queries.selectAll(userId, transactionMapper).asFlow().mapToList()
                .flowOn(backgroundDispatcher)

        override fun selectById(id: Uuid): Flow<TransactionDetail?> =
            queries.selectById(id, transactionDetailMapper).asFlow().mapToOneOrNull()
                .flowOn(backgroundDispatcher)

        override fun selectAllByAccountId(accountId: Uuid): Flow<List<Transaction>> =
            queries.selectAllByAccount(accountId, transactionMapper).asFlow().mapToList()
                .flowOn(backgroundDispatcher)

        override suspend fun deleteById(id: Uuid) {
            withContext(backgroundDispatcher) {
                queries.deleteById(id)
            }
        }

        override fun selectAllLikeName(userId: Uuid, query: String?): Flow<List<Transaction>> =
            queries.selectAllByName(userId, query ?: "", transactionMapper).asFlow().mapToList()
                .flowOn(backgroundDispatcher)

        override fun spendingCategoriesByDate(
            userId: Uuid,
            start: LocalDate,
            end: LocalDate
        ): Flow<List<Pair<String, Double>>> =
            queries.spendingCategoriesByDate(userId, start, end) { category, amount ->
                category!! to amount!!
            }.asFlow().mapToList().flowOn(backgroundDispatcher)
    }

    val transactionMapper: (
        Uuid,
        Uuid,
        Uuid,
        Uuid?,
        String?,
        String?,
        TransactionType,
        String,
        String?,
        LocalDate,
        Double,
        String?,
        Boolean?
    ) -> Transaction
        get() = {
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
    val transactionDetailMapper: (
        Uuid,
        Uuid,
        Uuid,
        Uuid?,
        String?,
        String?,
        TransactionType,
        String,
        String?,
        LocalDate,
        Double,
        Boolean?,
        String?,
        String?,
        String?,
        String?
    ) -> TransactionDetail
        get() = { id: Uuid,
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
