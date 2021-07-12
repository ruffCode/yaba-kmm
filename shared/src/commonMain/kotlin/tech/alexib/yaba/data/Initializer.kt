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
package tech.alexib.yaba.data

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.AllUserDataQuery
import tech.alexib.yaba.data.api.ApolloApi
import tech.alexib.yaba.data.api.ApolloResponse
import tech.alexib.yaba.data.api.dto.AccountDto
import tech.alexib.yaba.data.api.dto.ItemDto
import tech.alexib.yaba.data.api.dto.TransactionDto
import tech.alexib.yaba.data.api.dto.toDto
import tech.alexib.yaba.data.api.dto.toEntities
import tech.alexib.yaba.data.api.safeQuery
import tech.alexib.yaba.data.db.dao.AccountDao
import tech.alexib.yaba.data.db.dao.InstitutionDao
import tech.alexib.yaba.data.db.dao.ItemDao
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.db.dao.UserDao
import tech.alexib.yaba.data.repository.UserRepository
import tech.alexib.yaba.model.Institution
import tech.alexib.yaba.model.User
import tech.alexib.yaba.util.InvokeError
import tech.alexib.yaba.util.InvokeStarted
import tech.alexib.yaba.util.InvokeStatus
import tech.alexib.yaba.util.InvokeSuccess

interface Initializer {
    fun init(): Flow<InvokeStatus>
}

class InitializerImpl : Initializer, KoinComponent {

    private val apolloApi: ApolloApi by inject()
    private val log: Kermit by inject { parametersOf("Initializer") }
    private val accountDao: AccountDao by inject()
    private val institutionDao: InstitutionDao by inject()
    private val itemDao: ItemDao by inject()
    private val transactionDao: TransactionDao by inject()
    private val userDao: UserDao by inject()
    private val userRepository: UserRepository by inject()
    private val backgroundDispatcher: CoroutineDispatcher by inject()

    init {
        ensureNeverFrozen()
    }

    private suspend fun getRemoteUserData(): ApolloResponse<AllDataMappedResponse> {
        return withContext(backgroundDispatcher) {
            apolloApi.client().safeQuery(AllUserDataQuery()) {
                val data = it.me
                val userId = data.id as Uuid
                val user = User(userId, data.email)

                val transactions =
                    data.transactions.map { transaction ->
                        transaction.fragments.transaction.toDto()
                    }

                val institutions = data.items.map { item ->
                    with(item.institution) {
                        Institution(
                            institutionId = institutionId,
                            name = name,
                            logo = logo,
                            primaryColor = primaryColor ?: "#095aa6"
                        )
                    }
                }
                val items = data.items.map { item ->
                    ItemDto(
                        id = item.id as Uuid,
                        plaidInstitutionId = item.plaidInstitutionId,
                        userId = userId
                    )
                }
                val accounts = data.accounts.map { account -> account.fragments.account.toDto() }
                AllDataMappedResponse(
                    user,
                    transactions,
                    items,
                    accounts,
                    institutions
                )
            }.first()
        }
    }

    override fun init(): Flow<InvokeStatus> = flow {
        emit(InvokeStarted)
        val user = userRepository.currentUser().distinctUntilChanged().first()

        if (user == null) {
            when (val response = getRemoteUserData()) {
                is ApolloResponse.Success -> {
                    insertAllUserData(response.data)
                    emit(InvokeSuccess)
                }
                is ApolloResponse.Error -> log.e {
                    "Error retrieving user data: ${response.message}"
                }
            }
        } else {
            emit(InvokeSuccess)
        }
    }.catch {
        log.e(it) { "Initializer error ${it.message}" }
        emit(InvokeError(it))
    }

    private suspend fun insertAllUserData(data: AllDataMappedResponse) = runCatching {
        userDao.insert(data.user)
        data.institutions.forEach {
            institutionDao.insert(it)
        }

        itemDao.insert(data.items.toEntities())

        accountDao.insert(data.accounts.toEntities())

        transactionDao.insert(data.transactions.toEntities())
    }.fold(
        {
            log.d { "User data inserted" }
        },
        {
            log.e(it) {
                "Error inserting user data: ${it.message}"
            }
            throw it
        }
    )
}

private data class AllDataMappedResponse(
    val user: User,
    val transactions: List<TransactionDto>,
    val items: List<ItemDto>,
    val accounts: List<AccountDto>,
    val institutions: List<Institution>
)
