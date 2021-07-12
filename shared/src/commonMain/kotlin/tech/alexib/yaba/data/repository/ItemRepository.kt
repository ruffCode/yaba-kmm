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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.NewItemDataQuery
import tech.alexib.yaba.data.db.ItemEntity
import tech.alexib.yaba.data.api.ApolloApi
import tech.alexib.yaba.data.api.ApolloResponse
import tech.alexib.yaba.data.api.PlaidItemApi
import tech.alexib.yaba.data.api.dto.NewItemData
import tech.alexib.yaba.data.api.dto.toAccountWithTransactions
import tech.alexib.yaba.data.api.dto.toEntity
import tech.alexib.yaba.data.api.safeQuery
import tech.alexib.yaba.data.db.dao.AccountDao
import tech.alexib.yaba.data.db.dao.InstitutionDao
import tech.alexib.yaba.data.db.dao.ItemDao
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.model.Institution
import tech.alexib.yaba.model.PlaidItem
import tech.alexib.yaba.model.PlaidItemWithAccounts
import tech.alexib.yaba.model.User

interface ItemRepository {
    fun getAll(): Flow<List<PlaidItem>>
    fun getById(id: Uuid): Flow<PlaidItem>
    suspend fun getAllWithAccounts(): Flow<List<PlaidItemWithAccounts>>
    suspend fun newItemData(itemId: Uuid): Boolean
    suspend fun unlinkItem(id: Uuid)
}

internal class ItemRepositoryImpl : ItemRepository, KoinComponent {
    private val itemDao: ItemDao by inject()
    private val accountDao: AccountDao by inject()
    private val plaidItemApi: PlaidItemApi by inject()
    private val transactionDao: TransactionDao by inject()
    private val institutionDao: InstitutionDao by inject()
    private val userIdProvider: UserIdProvider by inject()
    private val log: Kermit by inject { parametersOf("ItemRepository") }
    private val apolloApi: ApolloApi by inject()
    private val backgroundDispatcher: CoroutineDispatcher by inject()

    init {
        ensureNeverFrozen()
    }

    override fun getAll(): Flow<List<PlaidItem>> = itemDao.selectAll(userIdProvider.userId.value)

    override fun getById(id: Uuid): Flow<PlaidItem> = itemDao.selectById(id)

    override suspend fun getAllWithAccounts(): Flow<List<PlaidItemWithAccounts>> =
        withContext(backgroundDispatcher) {
            combine(
                getAll(),
                accountDao.selectAll(userIdProvider.userId.value)
            ) { items, accounts ->
                items.map {
                    PlaidItemWithAccounts(
                        it,
                        accounts.filter { account -> account.itemId == it.id }
                    )
                }
            }
        }

    override suspend fun unlinkItem(id: Uuid) {
        plaidItemApi.unlink(id)
        itemDao.deleteById(id)
    }

    override suspend fun newItemData(itemId: Uuid): Boolean {
        val query = NewItemDataQuery(itemId)

        val response = apolloApi.client().safeQuery(query) { result ->
            val item = result.itemById

            NewItemData(
                item = PlaidItem(
                    id = item.id as Uuid,
                    name = item.institution.name,
                    base64Logo = item.institution.logo,
                    plaidInstitutionId = item.plaidInstitutionId
                ),
                accounts =
                item.accounts.map {
                    it.fragments.accountWithTransactions.toAccountWithTransactions()
                },
                user = User(result.me.id as Uuid, result.me.email)
            )
        }
        return when (val newData = response.firstOrNull()) {
            is ApolloResponse.Success -> {
                val data = newData.data
//                userDao.insert(data.user)
                institutionDao.insert(
                    Institution(
                        institutionId = data.item.plaidInstitutionId,
                        name = data.item.name,
                        logo = data.item.base64Logo,
                        primaryColor = "#095aa6"
                    )
                )
                itemDao.insert(
                    with(data.item) {
                        ItemEntity(
                            id = id,
                            plaid_institution_id = plaidInstitutionId,
                            data.user.id
                        )
                    }
                )

                val accounts = data.accounts.map {
                    it.account.toEntity()
                }
                val transactions =
                    data.accounts.flatMap {
                        it.transactions.map { transactionDto ->
                            transactionDto.toEntity()
                        }
                    }
                accountDao.insert(accounts)
                transactionDao.insert(transactions)
                true
            }
            is ApolloResponse.Error -> true
            null -> {
                log.d { "error fetching new item data: result was null" }
                true
            }

//        return when (val newData = plaidItemApi.newItemData(itemId).firstOrNull()) {
//            is Success -> {
//                val data = newData.data
//                userDao.insert(data.user)
//                institutionDao.insert(
//                    Institution(
//                        institutionId = data.item.plaidInstitutionId,
//                        name = data.item.name,
//                        logo = data.item.base64Logo,
//                        primaryColor = "#095aa6"
//                    )
//                )
//
//                itemDao.insert(
//                    with(data.item) {
//                        ItemEntity(
//                            id = id,
//                            plaid_institution_id = plaidInstitutionId,
//                            data.user.id
//                        )
//                    }
//                )
//
//                val accounts = data.accounts.map {
//                    it.account
//                }
//                val transactions = data.accounts.flatMap { it.transactions }
//                accountDao.insert(accounts)
//                transactionDao.insert(transactions)
//                true
//            }
//            is ErrorResult -> {
//                true
//            }
//            null -> {
//                log.d { "error fetching new item data: result was null" }
//                true
//            }
        }
    }
}
