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
package tech.alexib.yaba.android.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.repository.ItemRepository
import tech.alexib.yaba.model.PlaidItemWithAccounts

class AccountsScreenViewModel : ViewModel(), KoinComponent {

    private val log: Kermit by inject { parametersOf("AccountsScreenViewModel") }
    private val itemRepository: ItemRepository by inject()
    private val plaidItemsFlow = MutableStateFlow<List<PlaidItemWithAccounts>>(emptyList())
    private val loadingFlow = MutableStateFlow(false)
    val loading: StateFlow<Boolean>
        get() = loadingFlow

    val state: Flow<AccountsScreenState> =
        combine(plaidItemsFlow, loadingFlow) { items, loading ->
            AccountsScreenState(items, loading)
        }.stateIn(
            viewModelScope, started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccountsScreenState.Empty,
        )

    init {
        viewModelScope.launch {
            loadingFlow.emit(true)
            itemRepository.getAllWithAccounts().collect { items ->
                plaidItemsFlow.emit(items)
                loadingFlow.emit(false)
            }
        }
    }
}
