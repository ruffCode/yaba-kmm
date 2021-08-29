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

import kotlinx.coroutines.flow.firstOrNull
import tech.alexib.yaba.data.network.apollo.YabaApolloClient
import yaba.schema.PushTokenDeleteMutation
import yaba.schema.PushTokenInsertMutation

interface PushTokenApi {
    suspend fun save(token: String)
    suspend fun delete(token: String)

    class Impl(
        private val client: YabaApolloClient
    ) : PushTokenApi {
        override suspend fun save(token: String) {
            val mutation = PushTokenInsertMutation(token)
            client.mutate(mutation).firstOrNull()
        }

        override suspend fun delete(token: String) {
            val mutation = PushTokenDeleteMutation(token)
            client.mutate(mutation).firstOrNull()
        }
    }
}
