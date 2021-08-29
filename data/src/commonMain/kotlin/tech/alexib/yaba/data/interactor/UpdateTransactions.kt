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
package tech.alexib.yaba.data.interactor

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import tech.alexib.yaba.Interactor
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import tech.alexib.yaba.data.network.api.UserDataApi
import tech.alexib.yaba.data.repository.AccountRepository

class UpdateTransactions(
    private val log: Kermit,
    private val backgroundDispatcher: CoroutineDispatcher,
    private val userDataApi: UserDataApi,
    private val transactionDao: TransactionDao,
    private val accountRepository: AccountRepository,
) : Interactor<UpdateTransactions.Params>() {
    override suspend fun doWork(params: Params) {
        withContext(backgroundDispatcher) {
            when (val update = userDataApi.getTransactionsUpdate(params.updateId).firstOrNull()) {
                is Success -> {
                    update.data?.let {
                        val itemId = it.added?.firstOrNull()?.itemId
                        it.added?.let { transactions ->
                            transactionDao.insert(transactions)
                            log.d { "updateTransactions inserted ${transactions.size}" }
                        }
                        it.removed?.let { removedIds ->
                            log.d { "updateTransactions deleted ${removedIds.size}" }
                            removedIds.forEach { id ->
                                transactionDao.deleteById(id)
                            }
                        }
                        itemId?.let { id ->
                            accountRepository.updateByItemId(id)
                            log.d { "updateTransactions updateByItemId $id" }
                        }
                    }
                }
                is ErrorResult -> log.e { "Error fetching transactions updates: ${update.error}" }
                null -> log.e { "Error fetching transactions updates: response was null" }
            }
        }
    }

    data class Params(val updateId: Uuid)
}
