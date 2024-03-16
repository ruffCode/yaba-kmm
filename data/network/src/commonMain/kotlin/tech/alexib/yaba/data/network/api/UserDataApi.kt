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
import kotlinx.coroutines.flow.mapLatest
import tech.alexib.yaba.data.domain.DataResult
import tech.alexib.yaba.data.domain.dto.TransactionsUpdateDto
import tech.alexib.yaba.data.domain.dto.UserDataDto
import tech.alexib.yaba.data.network.apollo.YabaApolloClient
import tech.alexib.yaba.data.network.mapper.toDto
import yaba.schema.AllUserDataQuery
import yaba.schema.TestSubSubscription
import yaba.schema.TransactionsUpdateQuery

interface UserDataApi {
    fun getTransactionsUpdate(updateId: Uuid): Flow<DataResult<TransactionsUpdateDto?>>
    fun getAllUserData(): Flow<DataResult<UserDataDto>>
    fun testSub(): Flow<Int>

    class Impl(
        private val client: YabaApolloClient
    ) : UserDataApi {
        override fun getTransactionsUpdate(updateId: Uuid):
            Flow<DataResult<TransactionsUpdateDto?>> {
            val query = TransactionsUpdateQuery(updateId)
            return client.query(query) { it.toDto() }
        }

        override fun getAllUserData(): Flow<DataResult<UserDataDto>> {
            val query = AllUserDataQuery()
            return client.query(query) { it.toDto() }
        }

        override fun testSub(): Flow<Int> {
            val sub = TestSubSubscription("Alexi")
            return client.subscribe(sub).mapLatest { it.dataAssertNoErrors.testSub}
        }
    }
}
