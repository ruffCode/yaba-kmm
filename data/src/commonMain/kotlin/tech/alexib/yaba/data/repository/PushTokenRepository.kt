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
import kotlinx.coroutines.launch
import tech.alexib.yaba.data.network.api.PushTokenApi

interface PushTokenRepository {
    suspend fun save(token: String)
    fun delete(token: String)

    class Impl(
        private val pushTokenApi: PushTokenApi,
        private val backgroundDispatcher: CoroutineDispatcher
    ) : PushTokenRepository {

        override suspend fun save(token: String) {
            pushTokenApi.save(token)
        }

        override fun delete(token: String) {
            CoroutineScope(backgroundDispatcher).launch {
                pushTokenApi.delete(token)
            }
        }
    }
}
