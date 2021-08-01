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
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.data.db.mapper.toEntity
import tech.alexib.yaba.data.domain.dto.AccountDto
import tech.alexib.yaba.model.Account
import tech.alexib.yaba.model.AccountSubtype
import tech.alexib.yaba.model.AccountType

interface AccountDao {
    suspend fun insert(account: AccountDto)
    suspend fun insert(accounts: List<AccountDto>)
    fun currentCashBalance(userId: Uuid): Flow<Double>
    fun selectById(accountId: Uuid): Flow<Account?>
    suspend fun setHidden(id: Uuid, hidden: Boolean)
    fun selectAllNotHidden(userId: Uuid): Flow<List<Account>>
    fun selectAll(userId: Uuid): Flow<List<Account>>

    class Impl(
        database: YabaDb,
        private val backgroundDispatcher: CoroutineDispatcher,
    ) : AccountDao {
        private val accountQueries = database.accountQueries

        override suspend fun insert(account: AccountDto) {
            withContext(backgroundDispatcher) {
                accountQueries.insertAccount(
                    account.toEntity()
                )
            }
        }

        override suspend fun insert(accounts: List<AccountDto>) {
            withContext(backgroundDispatcher) {
                accounts.forEach {
                    accountQueries.insertAccount(it.toEntity())
                }
            }
        }

        override fun currentCashBalance(userId: Uuid): Flow<Double> =
            accountQueries.currentBalance(userId) { current -> current ?: 0.0 }.asFlow().mapToOne()
                .flowOn(backgroundDispatcher)

        override fun selectById(accountId: Uuid): Flow<Account?> =
            accountQueries.selectById(accountId, accountMapper).asFlow()
                .mapToOneOrNull()
                .flowOn(backgroundDispatcher)

        override suspend fun setHidden(id: Uuid, hidden: Boolean) {
            withContext(backgroundDispatcher) {
                accountQueries.setHidden(hidden, id)
            }
        }

        override fun selectAllNotHidden(userId: Uuid): Flow<List<Account>> =
            accountQueries.selectAllNotHidden(userId, accountMapper).asFlow().mapToList()
                .flowOn(backgroundDispatcher)

        override fun selectAll(userId: Uuid): Flow<List<Account>> =
            accountQueries.selectAll(userId, accountMapper).asFlow().mapToList()
                .flowOn(backgroundDispatcher)
    }

    val accountMapper: (
        Uuid,
        Uuid,
        Uuid?,
        String,
        String,
        Double,
        Double?,
        Double?,
        AccountType,
        AccountSubtype,
        Boolean,
        String?
    ) -> Account
        get() = { id: Uuid,
            item_id: Uuid,
            user_id: Uuid?,
            name: String,
            mask: String,
            current_balance: Double,
            available_balance: Double?,
            credit_limit: Double?,
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
                creditLimit = credit_limit,
                mask = mask,
                itemId = item_id,
                type = type,
                subtype = subtype,
                hidden = hidden,
                institutionName = institutionName ?: "unknown"
            )
        }
}
