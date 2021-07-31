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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import tech.alexib.yaba.data.db.dao.AccountDao
import tech.alexib.yaba.data.db.dao.ItemDao
import tech.alexib.yaba.data.network.api.PlaidItemApi
import tech.alexib.yaba.data.provider.UserIdProvider
import tech.alexib.yaba.model.PlaidItem
import tech.alexib.yaba.model.PlaidItemWithAccounts

interface ItemRepository {
    fun getById(id: Uuid): Flow<PlaidItem?>
    fun getAllWithAccounts(withHidden: Boolean = false): Flow<List<PlaidItemWithAccounts>>
    suspend fun unlinkItem(id: Uuid)
    fun userItemsCount(): Flow<Long>
}

internal class ItemRepositoryImpl(
    private val plaidItemApi: PlaidItemApi,
    private val itemDao: ItemDao,
    private val accountDao: AccountDao,
    private val userIdProvider: UserIdProvider,
) : ItemRepository {

    private fun getAll(): Flow<List<PlaidItem>> = itemDao.selectAll(userIdProvider.userId.value)
    override fun getById(id: Uuid): Flow<PlaidItem?> = flow {
        emitAll(itemDao.selectById(id))
    }

    override fun getAllWithAccounts(withHidden: Boolean): Flow<List<PlaidItemWithAccounts>> =
        combine(
            getAll(),
            if (withHidden) accountDao.selectAll(userIdProvider.userId.value) else accountDao.selectAllNotHidden(
                userIdProvider.userId.value
            )
        ) { items, accounts ->
            items.map {
                PlaidItemWithAccounts(
                    it,
                    accounts.filter { account -> account.itemId == it.id }
                )
            }
        }

    override suspend fun unlinkItem(id: Uuid) {
        plaidItemApi.unlink(id)
        itemDao.deleteById(id)
    }

    override fun userItemsCount(): Flow<Long> = flow {
        emitAll(itemDao.count(userIdProvider.userId.value))
    }
}
