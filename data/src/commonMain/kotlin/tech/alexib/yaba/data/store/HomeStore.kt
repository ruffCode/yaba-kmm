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
package tech.alexib.yaba.data.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import tech.alexib.yaba.data.interactor.PerformInitialSync
import tech.alexib.yaba.data.observer.ObserveCurrentCashBalance
import tech.alexib.yaba.data.observer.ObserveRecentTransactions
import tech.alexib.yaba.data.observer.ObserveUserItemsCount
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.util.ObservableLoadingCounter
import tech.alexib.yaba.util.collectInto

class HomeStore(
    private val observeRecentTransactions: ObserveRecentTransactions,
    private val observeCurrentCashBalance: ObserveCurrentCashBalance,
    private val observeUserItemsCount: ObserveUserItemsCount,
    private val performInitialSync: PerformInitialSync
) {

    private val loader = ObservableLoadingCounter()

    val state = combine(
        loader.observable,
        observeCurrentCashBalance.flow,
        observeRecentTransactions.flow,
        observeUserItemsCount.flow
    ) { loading, balance, transactions, itemCount ->
        HomeScreenState(
            loading, balance, transactions, itemCount
        )
    }

    fun init(scope: CoroutineScope) {
        observeCurrentCashBalance(Unit)
        observeRecentTransactions(Unit)
        observeUserItemsCount(Unit)
        scope.launch {
            performInitialSync(Unit).collectInto(loader)
        }
    }
}

data class HomeScreenState(
    val loading: Boolean = false,
    val currentCashBalance: Double? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val userItemCount: Long? = null
) {
    companion object {
        val Empty = HomeScreenState()
    }
}
