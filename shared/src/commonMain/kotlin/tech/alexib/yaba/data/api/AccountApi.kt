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
package tech.alexib.yaba.data.api

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.AccountByIdQuery
import tech.alexib.yaba.AccountByIdWithTransactionQuery
import tech.alexib.yaba.AccountSetHiddenMutation
import tech.alexib.yaba.AccountsByItemIdQuery
import tech.alexib.yaba.data.api.dto.AccountDto
import tech.alexib.yaba.data.api.dto.AccountWithTransactionsDto
import tech.alexib.yaba.data.api.dto.toAccountWithTransactions
import tech.alexib.yaba.data.api.dto.toDto
import tech.alexib.yaba.data.repository.DataResult
import tech.alexib.yaba.data.repository.ErrorResult
import tech.alexib.yaba.data.repository.Success

internal interface AccountApi {
    suspend fun setHideAccount(hide: Boolean, accountId: Uuid)
//    fun accountById(id: Uuid): Flow<DataResult<AccountDto>>
    fun accountByIdWithTransactions(id: Uuid): Flow<DataResult<AccountWithTransactionsDto>>
    fun accountsByItemId(itemId: Uuid): Flow<DataResult<List<AccountDto>>>
}

internal class AccountApiImpl : AccountApi, KoinComponent {

    private val apolloApi: ApolloApi by inject()
    private val log: Kermit by inject { parametersOf("AccountApi") }

    override suspend fun setHideAccount(hide: Boolean, accountId: Uuid) {
        val mutation = AccountSetHiddenMutation(id = accountId, hidden = hide)
        apolloApi.client().mutate(mutation).execute().firstOrNull()
    }

//    override fun accountById(id: Uuid): Flow<DataResult<AccountDto>> {
//        val query = AccountByIdQuery(id)
//        return apolloApi.client().safeQuery(query) {
//            it.accountById.fragments.account.toDto()
//        }.map {
//            when (it) {
//                is ApolloResponse.Success -> Success(
//                    it.data
//                )
//                is ApolloResponse.Error -> ErrorResult(
//                    it.message
//                )
//            }
//        }
//    }

    override fun accountByIdWithTransactions(id: Uuid):
        Flow<DataResult<AccountWithTransactionsDto>> {
        val query = AccountByIdWithTransactionQuery(id)

        return apolloApi.client().safeQuery(query) {
            it.accountById.fragments.accountWithTransactions.toAccountWithTransactions()
        }.map {
            when (it) {
                is ApolloResponse.Success -> Success(it.data)
                is ApolloResponse.Error -> ErrorResult(it.message)
            }
        }
    }

    override fun accountsByItemId(itemId: Uuid): Flow<DataResult<List<AccountDto>>> {
        val query = AccountsByItemIdQuery(itemId)
        return apolloApi.client().safeQuery(query) { data ->
            data.accountsByItemId.map { it.fragments.account.toDto() }
        }.map {
            when (it) {
                is ApolloResponse.Success -> Success(it.data)
                is ApolloResponse.Error -> ErrorResult(it.message)
            }
        }
    }
}
