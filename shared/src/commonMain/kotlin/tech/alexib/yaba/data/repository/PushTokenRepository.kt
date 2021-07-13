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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.PushTokenDeleteMutation
import tech.alexib.yaba.PushTokenInsertMutation
import tech.alexib.yaba.data.api.ApolloApi

interface PushTokenRepository {
    suspend fun save(token: String)
    fun delete(token: String)
}

class PushTokenRepositoryImpl : PushTokenRepository, KoinComponent {
    private val apolloApi: ApolloApi by inject()
    private val backgroundDispatcher: CoroutineDispatcher by inject()
    override suspend fun save(token: String) {
        val mutation = PushTokenInsertMutation(token)
        apolloApi.client().mutate(mutation).execute().firstOrNull()
    }

    override fun delete(token: String) {
        CoroutineScope(backgroundDispatcher).launch {
            val mutation = PushTokenDeleteMutation(token)
            apolloApi.client().mutate(mutation).execute().firstOrNull()
        }
    }
}
