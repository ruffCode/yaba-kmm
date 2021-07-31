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
package tech.alexib.yaba.data.network.api

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import tech.alexib.yaba.AccountByIdWithTransactionQuery
import tech.alexib.yaba.AccountSetHiddenMutation
import tech.alexib.yaba.AccountsByItemIdQuery
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.dto.AccountDto
import tech.alexib.yaba.data.domain.dto.AccountWithTransactionsDto
import tech.alexib.yaba.data.network.apollo.YabaApolloClient
import tech.alexib.yaba.data.network.mapper.toAccountWithTransactions
import tech.alexib.yaba.data.network.mapper.toDto

interface AccountApi {
    suspend fun setHideAccount(hide: Boolean, accountId: Uuid)
    fun accountByIdWithTransactions(id: Uuid): Flow<DataResult<AccountWithTransactionsDto>>
    fun accountsByItemId(itemId: Uuid): Flow<DataResult<List<AccountDto>>>

    class Impl(
        private val client: YabaApolloClient
    ) : AccountApi {
        override suspend fun setHideAccount(hide: Boolean, accountId: Uuid) {
            val mutation = AccountSetHiddenMutation(id = accountId, hidden = hide)
            client.mutate(mutation).firstOrNull()
        }

        override fun accountByIdWithTransactions(id: Uuid): Flow<DataResult<AccountWithTransactionsDto>> {
            val query = AccountByIdWithTransactionQuery(id)
            return client.query(query) { it.accountById.fragments.accountWithTransactions.toAccountWithTransactions() }
        }

        override fun accountsByItemId(itemId: Uuid): Flow<DataResult<List<AccountDto>>> {
            val query = AccountsByItemIdQuery(itemId)
            return client.query(query) { data ->
                data.accountsByItemId.map { it.fragments.account.toDto() }
            }
        }
    }
}
