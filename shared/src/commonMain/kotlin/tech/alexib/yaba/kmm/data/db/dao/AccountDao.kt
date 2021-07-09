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
package tech.alexib.yaba.kmm.data.db.dao

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.AccountEntity
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.data.db.sqldelight.transactionWithContext
import tech.alexib.yaba.kmm.model.Account
import tech.alexib.yaba.kmm.model.AccountSubtype
import tech.alexib.yaba.kmm.model.AccountType

internal interface AccountDao {
    suspend fun insert(account: AccountEntity)
    suspend fun insert(accounts: List<AccountEntity>)
    suspend fun selectAll(userId: Uuid): Flow<List<Account>>
    suspend fun selectById(accountId: Uuid): Flow<Account?>
    suspend fun selectAllByItemId(itemId: Uuid): Flow<List<Account>>
    suspend fun availableBalance(userId: Uuid): Flow<Double>
    suspend fun currentBalance(userId: Uuid): Flow<Double>
    suspend fun setHidden(id: Uuid, hidden: Boolean)
}

internal class AccountDaoImpl(
    private val database: YabaDb,
    private val backgroundDispatcher: CoroutineDispatcher,
) : AccountDao {
    private val accountQueries = database.accountQueries

    init {
        ensureNeverFrozen()
    }

    override suspend fun insert(account: AccountEntity) {
        withContext(backgroundDispatcher) {
            accountQueries.insertAccount(
                account
            )
        }
    }

    override suspend fun insert(accounts: List<AccountEntity>) {
        database.transactionWithContext(backgroundDispatcher) {
            accounts.forEach {
                accountQueries.insertAccount(it)
            }
        }
    }

    override suspend fun selectAll(userId: Uuid): Flow<List<Account>> {
        return accountQueries.selectAll(userId, accountMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)
    }

    override suspend fun selectById(accountId: Uuid): Flow<Account?> =
        accountQueries.selectById(accountId, accountMapper).asFlow()
            .mapToOneOrNull()
            .flowOn(backgroundDispatcher)

    override suspend fun selectAllByItemId(itemId: Uuid): Flow<List<Account>> =
        accountQueries.selectAllByItemId(itemId, accountMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)

    override suspend fun availableBalance(userId: Uuid): Flow<Double> =
        accountQueries.availableBalance(userId) { available -> available ?: 0.0 }.asFlow()
            .mapToOne()
            .flowOn(backgroundDispatcher)

    override suspend fun setHidden(id: Uuid, hidden: Boolean) {
        withContext(backgroundDispatcher) {
            accountQueries.setHidden(hidden, id)
        }
    }

    override suspend fun currentBalance(userId: Uuid): Flow<Double> =
        accountQueries.currentBalance(userId) { current -> current ?: 0.0 }.asFlow().mapToOne()
            .flowOn(backgroundDispatcher)

    companion object {
        private val accountMapper =
            { id: Uuid,
                item_id: Uuid,
                _: Uuid?,
                name: String,
                mask: String,
                current_balance: Double,
                available_balance: Double,
                type: AccountType,
                subtype: AccountSubtype,
                hidden: Boolean,
                institutionName: String?

                ->
                Account(
                    id = id,
                    name = name,
                    currentBalance = current_balance,
                    availableBalance = available_balance,
                    mask = mask,
                    itemId = item_id,
                    type = type,
                    subtype = subtype,
                    hidden = hidden,
                    institutionName = institutionName ?: "unknown"
                )
            }
    }
}
