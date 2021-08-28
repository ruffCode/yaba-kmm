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
package tech.alexib.yaba.data.interactor

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import tech.alexib.yaba.Interactor
import tech.alexib.yaba.data.domain.ErrorResult
import tech.alexib.yaba.data.domain.Success
import tech.alexib.yaba.data.network.api.PlaidItemApi
import tech.alexib.yaba.data.repository.ItemRepository

class AddItem(
    private val log: Kermit,
    private val backgroundDispatcher: CoroutineDispatcher,
    private val plaidItemApi: PlaidItemApi,
    private val itemRepository: ItemRepository
) : Interactor<AddItem.Params>() {
    override suspend fun doWork(params: Params) {

        withContext(backgroundDispatcher) {
            when (val result = plaidItemApi.fetchNewItemData(params.itemId).first()) {
                is Success -> runCatching {
                    itemRepository.insert(result.data)
                }.fold(
                    {
                        log.d { "New Item inserted" }
                    },
                    {
                        log.e(it) {
                            "Error inserting new item data: ${it.message}"
                        }
                        throw it
                    }
                )
                is ErrorResult -> log.e { result.error }
            }
        }
    }

    data class Params(val itemId: Uuid)
}
