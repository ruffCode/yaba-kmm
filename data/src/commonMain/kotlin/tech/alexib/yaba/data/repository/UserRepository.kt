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

import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import tech.alexib.yaba.data.db.dao.UserDao
import tech.alexib.yaba.data.provider.UserIdProvider
import tech.alexib.yaba.model.User
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface UserRepository {
    suspend fun currentUser(): Flow<User?>
    suspend fun deleteCurrentUser()
}

internal class UserRepositoryImpl(
    private val userIdProvider: UserIdProvider,
    private val log: Kermit,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun currentUser(): Flow<User?> {
        return flow {
            withTimeout(5.toDuration(DurationUnit.SECONDS)) {
                userIdProvider.getCurrentUserId().collect { id ->
                    emitAll(userDao.selectById(id))
                }
            }
        }
    }

    override suspend fun deleteCurrentUser() {
        userDao.deleteById(userIdProvider.userId.value)
        log.d { "User deleted" }
    }
}
