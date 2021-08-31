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

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import tech.alexib.yaba.Interactor
import tech.alexib.yaba.data.repository.ItemRepository

class SetAccountsToHide(
    private val itemRepository: ItemRepository,
    private val backgroundDispatcher: CoroutineDispatcher
) : Interactor<SetAccountsToHide.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(backgroundDispatcher) {
            itemRepository.setAccountsToHideSync(params.itemId, params.plaidAccountIds)
        }
    }

    data class Params(val itemId: Uuid, val plaidAccountIds: List<String>)
}
