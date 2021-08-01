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
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import tech.alexib.yaba.data.db.ItemEntityQueries
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.data.db.mapper.toEntity
import tech.alexib.yaba.data.db.util.transactionWithContext
import tech.alexib.yaba.data.domain.dto.ItemDto
import tech.alexib.yaba.model.PlaidItem

interface ItemDao {
    suspend fun insert(item: ItemDto)
    suspend fun insert(items: List<ItemDto>)
    fun count(userId: Uuid): Flow<Long>
    fun selectAll(userId: Uuid): Flow<List<PlaidItem>>
    fun selectById(id: Uuid): Flow<PlaidItem?>
    suspend fun deleteById(id: Uuid)
    val itemMapper: (Uuid, String, Uuid, String, String) -> PlaidItem
        get() = { id: Uuid,
            plaid_institution_id: String,
            user_id: Uuid,
            name: String,
            logo: String
            ->
            PlaidItem(
                id = id,
                plaidInstitutionId = plaid_institution_id,
                name = name,
                base64Logo = logo
            )
        }

    class Impl(
        private val database: YabaDb,
        private val backgroundDispatcher: CoroutineDispatcher,
    ) : ItemDao {

        init {
            ensureNeverFrozen()
        }

        private val queries: ItemEntityQueries = database.itemEntityQueries
        override suspend fun insert(item: ItemDto) {
            withContext(backgroundDispatcher) {
                queries.insert(
                    item.toEntity()
                )
            }
        }

        override suspend fun insert(items: List<ItemDto>) {
            database.transactionWithContext(backgroundDispatcher) {
                items.forEach {
                    queries.insert(it.toEntity())
                }
            }
        }

        override fun count(userId: Uuid): Flow<Long> =
            queries.count(userId).asFlow().mapToOne().flowOn(backgroundDispatcher)

        override fun selectAll(userId: Uuid): Flow<List<PlaidItem>> =
            queries.selectAll(userId, itemMapper).asFlow().mapToList()
                .flowOn(backgroundDispatcher)

        override fun selectById(id: Uuid): Flow<PlaidItem?> =
            queries.selectById(id, itemMapper).asFlow().mapToOneOrNull()
                .flowOn(backgroundDispatcher)

        override suspend fun deleteById(id: Uuid) {
            withContext(backgroundDispatcher) {
                queries.deleteById(id)
            }
        }
    }
}
