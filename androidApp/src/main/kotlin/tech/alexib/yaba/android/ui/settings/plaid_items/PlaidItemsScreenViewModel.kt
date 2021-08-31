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
package tech.alexib.yaba.android.ui.settings.plaid_items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.data.store.PlaidItemsScreenState
import tech.alexib.yaba.data.store.PlaidItemsStore
import tech.alexib.yaba.util.stateInDefault

class PlaidItemsScreenViewModel : ViewModel(), KoinComponent {

    private val store: PlaidItemsStore by inject()

    val state = store.state.stateInDefault(viewModelScope, PlaidItemsScreenState.Empty)
//    private val log: Kermit by inject { parametersOf("PlaidItemsScreenViewModel") }
//    private val itemRepository: ItemRepository by inject()
//    private val plaidItemsFlow = MutableStateFlow<List<PlaidItemWithAccounts>>(emptyList())
//    private val loadingFlow = MutableStateFlow(false)
//    val loading: StateFlow<Boolean>
//        get() = loadingFlow
//
//    val state: StateFlow<PlaidItemsScreenState> =
//        combine(plaidItemsFlow, loadingFlow) { items, loading ->
//            PlaidItemsScreenState(items, loading)
//        }.stateInDefault(viewModelScope, PlaidItemsScreenState.Empty)
//
//    init {
//        viewModelScope.launch {
//            loadingFlow.emit(true)
//            itemRepository.getAllWithAccounts(true).collect { items ->
//                plaidItemsFlow.emit(items)
//                loadingFlow.emit(false)
//            }
//        }
//    }
}
