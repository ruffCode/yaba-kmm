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
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.model.TransactionDetail

interface TransactionRepository {
    fun recentTransactions(): Flow<List<Transaction>>
    fun count(): Flow<Long>
    fun selectAll(): Flow<List<Transaction>>
    fun selectById(id: Uuid): Flow<TransactionDetail>
}

internal class TransactionRepositoryImpl : TransactionRepository, KoinComponent {
    private val log: Kermit by inject { parametersOf("TransactionRepository") }
    private val userIdProvider: UserIdProvider by inject()
    private val dao: TransactionDao by inject()

    init {
        ensureNeverFrozen()
    }

    override fun recentTransactions(): Flow<List<Transaction>> = flow {
        emitAll(dao.selectRecent(userIdProvider.userId.value))
    }

    override fun count(): Flow<Long> = flow {
        emitAll(dao.count(userIdProvider.userId.value))
    }

    override fun selectAll(): Flow<List<Transaction>> =
        flow { emitAll(dao.selectAll(userIdProvider.userId.value)) }

    override fun selectById(id: Uuid): Flow<TransactionDetail> = flow {
        emitAll(dao.selectById(id))
    }
}
