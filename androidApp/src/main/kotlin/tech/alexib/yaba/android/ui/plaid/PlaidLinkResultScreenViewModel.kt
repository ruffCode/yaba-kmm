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
package tech.alexib.yaba.android.ui.plaid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.store.PlaidLinkResultScreenState
import tech.alexib.yaba.data.store.PlaidLinkResultStore
import tech.alexib.yaba.data.store.PlaidLinkScreenResult
import tech.alexib.yaba.util.stateInDefault

class PlaidLinkResultScreenViewModel : ViewModel(), KoinComponent {

    val store: PlaidLinkResultStore by inject { parametersOf(viewModelScope) }

    //    private val loader = ObservableLoadingCounter()
//    private val setAccountsToHide: SetAccountsToHide by inject()
//    private val addItem: AddItem by inject()

    private val log: Kermit by inject { parametersOf("PlaidLinkResultScreenViewModel") }
    private val accountsFlow = MutableStateFlow<List<PlaidLinkScreenResult.Account>>(emptyList())


    //    private val loadingFlow = MutableStateFlow(false)
//    val loading: StateFlow<Boolean>
//        get() = loadingFlow
    private val shouldNavigateHomeFlow = MutableStateFlow(false)
//    val shouldNavigateHome: StateFlow<Boolean>
//        get() = shouldNavigateHomeFlow
//    val accounts: StateFlow<List<PlaidLinkScreenResult.Account>>
//        get() = accountsFlow

    fun init(plaidLinkScreenResult: PlaidLinkScreenResult) {
        store.init(plaidLinkScreenResult)
    }

    val state = store.state.stateInDefault(viewModelScope, PlaidLinkResultScreenState.Empty)


//    fun setAccountShown(plaidAccountId: String, show: Boolean) {
//        val currentAccounts = accountsFlow.value.toMutableList()
//        val currentItem = currentAccounts.first { it.plaidAccountId == plaidAccountId }
//        currentAccounts[currentAccounts.indexOf(currentItem)] = currentItem.copy(show = show)
//        accountsFlow.value = currentAccounts
//    }
//
//    val state = combine(
//        loader.observable,
//        accountsFlow,
//        shouldNavigateHomeFlow
//    ) { loading, accounts, shouldNavigate ->
//        PlaidLinkResultScreenState(loading, shouldNavigate, accounts)
//    }.stateInDefault(viewModelScope, PlaidLinkResultScreenState.Empty)
//
//    fun submitAccountsToHide() {
//        val accountsToHide = accountsFlow.value.filter { !it.show }.map { it.plaidAccountId }
//        viewModelScope.launch(Dispatchers.Default) {
//
//            setAccountsToHide(SetAccountsToHide.Params(itemId, accountsToHide)).collect {
//                when (it) {
//                    is InvokeStarted -> loader.addLoader()
//                    is InvokeSuccess -> addItem(AddItem.Params(itemId)).collect { status ->
//                        if (status != InvokeStarted) {
//                            loader.removeLoader()
//                            shouldNavigateHomeFlow.emit(true)
//                        }
//                    }
//                    is InvokeError -> {
//                        log.e(it.throwable) { "Error loading transactions" }
//                    }
//                }
//            }
//        }
//    }
}
