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

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.provider.UserIdProvider
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.model.TransactionDetail

interface TransactionRepository {
    fun recentTransactions(): Flow<List<Transaction>>
    fun getAll(query: String? = null): Flow<List<Transaction>>
    fun getById(id: Uuid): Flow<TransactionDetail?>
    fun getAllByAccountId(accountId: Uuid): Flow<List<Transaction>>
    fun spendingCategoriesByDate(start: LocalDate, end: LocalDate): Flow<List<Pair<String, Double>>>
}

internal class TransactionRepositoryImpl(
    private val userIdProvider: UserIdProvider,
    private val dao: TransactionDao,
) : TransactionRepository {

    override fun recentTransactions(): Flow<List<Transaction>> = flow {
        emitAll(dao.selectRecent(userIdProvider.userId.value))
    }

    override fun getAll(query: String?): Flow<List<Transaction>> =
        flow {
            emitAll(
                if (!query.isNullOrEmpty()) dao.selectAllLikeName(
                    userIdProvider.userId.value,
                    query
                ) else dao.selectAll(userIdProvider.userId.value)
            )
        }

    override fun getById(id: Uuid): Flow<TransactionDetail?> = flow {
        emitAll(dao.selectById(id))
    }

    override fun getAllByAccountId(accountId: Uuid): Flow<List<Transaction>> = flow {
        emitAll(dao.selectAllByAccountId(accountId))
    }

    override fun spendingCategoriesByDate(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<Pair<String, Double>>> = flow {
        emitAll(dao.spendingCategoriesByDate(userIdProvider.userId.value, start, end))
    }
}
