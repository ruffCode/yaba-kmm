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
package tech.alexib.yaba.data.db.dao

import co.touchlab.stately.ensureNeverFrozen
import com.benasher44.uuid.Uuid
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.UserEntity
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.data.db.mapper.toEntity
import tech.alexib.yaba.data.db.util.transactionWithContext
import tech.alexib.yaba.data.domain.dto.UserDataDto
import tech.alexib.yaba.model.User

interface UserDao {
    suspend fun insert(user: User)
    fun selectById(userId: Uuid): Flow<User?>
    suspend fun deleteById(userId: Uuid)
    suspend fun insertUserData(userData: UserDataDto)

    class Impl(
        private val database: YabaDb,
        private val backgroundDispatcher: CoroutineDispatcher
    ) : UserDao {
        private val queries = database.userEntityQueries

        init {
            ensureNeverFrozen()
        }

        override suspend fun insert(user: User) {
            withContext(backgroundDispatcher) {
                queries.insert(UserEntity(user.id, user.email))
            }
        }

        override fun selectById(userId: Uuid): Flow<User?> =
            queries.selectById(userId) { id: Uuid, email: String -> User(id, email) }.asFlow()
                .mapToOneOrNull().flowOn(backgroundDispatcher)

        override suspend fun deleteById(userId: Uuid) {
            withContext(backgroundDispatcher) {
                queries.deleteById(userId)
            }
        }

        override suspend fun insertUserData(userData: UserDataDto) {
            database.transactionWithContext(backgroundDispatcher) {
                database.userEntityQueries.insert(userData.user.toEntity())
                userData.institutions.forEach {
                    database.institutionEntityQueries.insertInstitution(it.toEntity())
                }
                userData.items.forEach {
                    database.itemEntityQueries.insert(it.toEntity())
                }
                userData.accounts.forEach {
                    database.accountQueries.insertAccount(it.toEntity())
                }
                userData.transactions.forEach {
                    database.transactionQueries.insert(it.toEntity())
                }
            }
        }
    }
}
