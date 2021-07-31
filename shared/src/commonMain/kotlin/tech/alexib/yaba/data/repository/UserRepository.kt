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
import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.db.dao.UserDao
import tech.alexib.yaba.model.User
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

interface UserRepository {
    suspend fun currentUser(): Flow<User?>
//    suspend fun currentUser(id: Uuid): User?
    suspend fun deleteCurrentUser()
}

internal class UserRepositoryImpl(private val userIdProvider: UserIdProvider) :
    UserRepository,
    KoinComponent {

    private val log: Kermit by inject { parametersOf("UserRepository") }
    private val userDao: UserDao by inject()

    init {
        ensureNeverFrozen()
    }

    @ExperimentalTime
    override suspend fun currentUser(): Flow<User?> {
        return flow {
            withTimeout(5.toDuration(DurationUnit.SECONDS)) {
                userIdProvider.getCurrentUserId().collect { id ->
                    emitAll(userDao.selectById(id))
                }
            }
        }
    }

//    override suspend fun currentUser(id: Uuid): User? = userDao.selectById(id).firstOrNull()

    override suspend fun deleteCurrentUser() {
        userDao.deleteById(userIdProvider.userId.value)
        log.d { "User deleted" }
    }
}
