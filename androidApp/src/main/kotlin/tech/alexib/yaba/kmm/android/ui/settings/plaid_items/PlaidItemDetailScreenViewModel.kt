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
package tech.alexib.yaba.kmm.android.ui.settings.plaid_items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.kmm.data.repository.AccountRepository
import tech.alexib.yaba.kmm.data.repository.ItemRepository

class PlaidItemDetailScreenViewModel : ViewModel(), KoinComponent {

    private val accountRepository: AccountRepository by inject()
    private val itemRepository: ItemRepository by inject()
    fun unlinkItem(itemId: Uuid) {
        viewModelScope.launch {
            itemRepository.unlinkItem(itemId)
        }
    }

    fun setAccountHidden(hidden: Boolean, accountId: Uuid) {
        viewModelScope.launch {
            if (hidden) {
                accountRepository.hide(accountId)
            } else {
                accountRepository.show(accountId)
            }
        }
    }
}
