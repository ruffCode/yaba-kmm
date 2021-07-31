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
package tech.alexib.yaba.data.provider

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.launch
import tech.alexib.yaba.data.settings.AuthSettings

internal interface UserIdProvider {
    val userId: StateFlow<Uuid>
    fun getCurrentUserId(): Flow<Uuid>

    class Impl(
        private val authSettings: AuthSettings,
        private val backgroundDispatcher: CoroutineDispatcher,
        private val log: Kermit
    ) : UserIdProvider {
        private val userIdFlow = MutableStateFlow<Uuid>(defaultUserId)
        override val userId: StateFlow<Uuid>
            get() = userIdFlow

        init {
            ensureNeverFrozen()

            CoroutineScope(backgroundDispatcher).launch {
                authSettings.userId().distinctUntilChanged().collectLatest { userId ->
                    log.d { "userId set $userId" }
                    userId?.let { userIdFlow.emit(it) }
                }
            }
        }

        override fun getCurrentUserId(): Flow<Uuid> {
            return channelFlow<Uuid> {
                userIdFlow.dropWhile { it == defaultUserId }.collectLatest {
                    launch(backgroundDispatcher) {
                        send(it)
                    }
                }
            }
        }

        companion object {
            val defaultUserId = uuidFrom("aa1e60bd-0a48-4e8d-800e-237f42d4a793")
        }
    }
}
