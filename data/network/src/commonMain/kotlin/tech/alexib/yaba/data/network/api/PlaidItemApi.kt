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

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.dto.NewItemDto
import tech.alexib.yaba.data.network.apollo.YabaApolloClient
import tech.alexib.yaba.data.network.mapper.toDto
import tech.alexib.yaba.data.network.mapper.toMutation
import tech.alexib.yaba.data.network.mapper.toResponse
import tech.alexib.yaba.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.model.request.PlaidLinkEventCreateRequest
import tech.alexib.yaba.model.response.CreateLinkTokenResponse
import tech.alexib.yaba.model.response.PlaidItemCreateResponse
import yaba.schema.CreateItemMutation
import yaba.schema.CreateLinkTokenMutation
import yaba.schema.NewItemDataQuery
import yaba.schema.SetAccountsToHideMutation
import yaba.schema.UnlinkItemMutation

interface PlaidItemApi {
    fun createLinkToken(): Flow<DataResult<CreateLinkTokenResponse>>
    fun createPlaidItem(request: PlaidItemCreateRequest): Flow<DataResult<PlaidItemCreateResponse>>
    fun sendLinkEvent(request: PlaidLinkEventCreateRequest)
    fun fetchNewItemData(itemId: Uuid): Flow<DataResult<NewItemDto>>
    suspend fun setAccountsToHide(itemId: Uuid, plaidAccountIds: List<String>)
    suspend fun unlink(itemId: Uuid)

    class Impl(
        private val client: YabaApolloClient,
        private val log: Kermit
    ) : PlaidItemApi {
        override fun createLinkToken(): Flow<DataResult<CreateLinkTokenResponse>> {
            val mutation = CreateLinkTokenMutation()
            return client.mutate(mutation) { result: CreateLinkTokenMutation.Data ->
                CreateLinkTokenResponse(result.createLinkToken.linkToken)
            }
        }

        override fun createPlaidItem(request: PlaidItemCreateRequest):
            Flow<DataResult<PlaidItemCreateResponse>> {
            val mutation = CreateItemMutation(request.institutionId, request.publicToken)
            return client.mutate(mutation) { result -> result.toResponse() }
        }

        override fun sendLinkEvent(request: PlaidLinkEventCreateRequest) {
            CoroutineScope(client.backgroundDispatcher).launch {
                val mutation = request.toMutation()
                runCatching {
                    client.mutate(mutation).firstOrNull()
                }.getOrElse {
                    log.e { "error sending link event ${it.message}" }
                }
            }
        }

        override suspend fun unlink(itemId: Uuid) {
            val mutation = UnlinkItemMutation(itemId)
            CoroutineScope(client.backgroundDispatcher).launch {
                runCatching {
                    client.mutate(mutation).firstOrNull()
                }.getOrElse {
                    log.e { "error unlinking item ${it.message}" }
                }
            }
        }

        override fun fetchNewItemData(itemId: Uuid): Flow<DataResult<NewItemDto>> {
            val query = NewItemDataQuery(itemId)
            return client.query(query) { it.toDto() }
        }

        override suspend fun setAccountsToHide(itemId: Uuid, plaidAccountIds: List<String>) {
            val mutation = SetAccountsToHideMutation(itemId, plaidAccountIds)
            client.mutate(mutation) { it.setAccountsToHide }.first().getOrThrow()
        }
    }
}
