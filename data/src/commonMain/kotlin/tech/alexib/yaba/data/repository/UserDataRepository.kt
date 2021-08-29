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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.db.dao.UserDao
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import tech.alexib.yaba.data.domain.dto.NewItemDto
import tech.alexib.yaba.data.domain.dto.UserDataDto
import tech.alexib.yaba.data.network.api.PlaidItemApi
import tech.alexib.yaba.data.network.api.UserDataApi
import tech.alexib.yaba.util.InvokeError
import tech.alexib.yaba.util.InvokeStarted
import tech.alexib.yaba.util.InvokeStatus
import tech.alexib.yaba.util.InvokeSuccess

interface UserDataRepository {
    fun handleNewItem(itemId: Uuid): Flow<InvokeStatus>
    suspend fun handleUpdate(updateId: Uuid)
    fun handleInitialSync(): Flow<InvokeStatus>
}

internal class UserDataRepositoryImpl(
    private val plaidItemApi: PlaidItemApi,
    private val userDataApi: UserDataApi,
    private val transactionDao: TransactionDao,
    private val accountRepository: AccountRepository,
    private val log: Kermit,
    private val backgroundDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepository,
    private val userDao: UserDao,
    private val itemRepository: ItemRepository,
) : UserDataRepository {

    private suspend fun insertNewItemData(data: NewItemDto) = runCatching {
        itemRepository.insert(data)
    }.fold(
        {
            log.d { "New Item inserted" }
        },
        {
            log.e(it) {
                "Error inserting new item data: ${it.message}"
            }
            throw it
        }
    )

    override fun handleNewItem(itemId: Uuid): Flow<InvokeStatus> = flow {
        emit(InvokeStarted)
        when (val result = plaidItemApi.fetchNewItemData(itemId).firstOrNull()) {
            is Success -> {
                insertNewItemData(result.data)
                emit(InvokeSuccess)
            }
            is ErrorResult -> {
                log.e { result.error }
                emit(InvokeSuccess)
            }
            null -> emit(InvokeSuccess)
        }
    }.catch {
        log.e(it) { "New item insert error ${it.message}" }
        emit(InvokeError(it))
    }

    private suspend fun deleteTransactions(ids: List<Uuid>) {
        ids.forEach {
            transactionDao.deleteById(it)
        }
    }

    override suspend fun handleUpdate(updateId: Uuid) {
        withContext(backgroundDispatcher) {
            when (val update = userDataApi.getTransactionsUpdate(updateId).firstOrNull()) {
                is Success -> {
                    update.data?.let {
                        val itemId = it.added?.firstOrNull()?.itemId
                        it.added?.let { transactions ->
                            transactionDao.insert(transactions)
                            log.d { "updateTransactions inserted ${transactions.size}" }
                        }
                        it.removed?.let { removedIds ->
                            log.d { "updateTransactions deleted ${removedIds.size}" }
                            deleteTransactions(removedIds)
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

    override fun handleInitialSync(): Flow<InvokeStatus> = flow {
        emit(InvokeStarted)
        val user = userRepository.currentUser().distinctUntilChanged().first()
        if (user == null) {
            when (val result = userDataApi.getAllUserData().firstOrNull()) {
                is Success -> {
                    handleSaveUserData(result.data)
                    emit(InvokeSuccess)
                }
                is ErrorResult -> {
                    log.e { result.error }
                    emit(InvokeSuccess)
                }
                null -> emit(InvokeSuccess)
            }
        } else emit(InvokeSuccess)
    }.catch {
        log.e(it) { "Initializer error ${it.message}" }
        emit(InvokeError(it))
    }

    private suspend fun handleSaveUserData(data: UserDataDto) = runCatching {
        userDao.insertUserData(data)
    }.fold(
        {
            log.d { "Initial sync completed" }
        },
        {
            log.e(it) {
                "Error inserting user data: ${it.message}"
            }
            throw it
        }
    )
}
