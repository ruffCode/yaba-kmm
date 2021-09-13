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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import tech.alexib.yaba.data.Immutable
import tech.alexib.yaba.data.interactor.PerformInitialSync
import tech.alexib.yaba.data.observer.ObserveCurrentCashBalance
import tech.alexib.yaba.data.observer.ObserveRecentTransactions
import tech.alexib.yaba.data.observer.ObserveSpendingCategoriesByDate
import tech.alexib.yaba.data.observer.ObserveUserItemsCount
import tech.alexib.yaba.data.util.SupervisorScope
import tech.alexib.yaba.model.AllCategoriesSpend
import tech.alexib.yaba.model.RangeOption
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.util.ObservableLoadingCounter
import tech.alexib.yaba.util.collectInto

class HomeStore(
    private val observeRecentTransactions: ObserveRecentTransactions,
    private val observeCurrentCashBalance: ObserveCurrentCashBalance,
    private val observeUserItemsCount: ObserveUserItemsCount,
    private val performInitialSync: PerformInitialSync,
    private val observeSpendingCategoriesByDate: ObserveSpendingCategoriesByDate,
    dispatcher: CoroutineDispatcher
) {

    private val loader = ObservableLoadingCounter()
    private val coroutineScope = SupervisorScope(dispatcher)

    private val actions = MutableSharedFlow<HomeScreenAction>()

    private val spendingCategoryDateRange =
        MutableStateFlow(
            ObserveSpendingCategoriesByDate.Params(
                null
            )
        )

    val state = combine(
        loader.observable,
        observeCurrentCashBalance.flow,
        observeRecentTransactions.flow,
        observeUserItemsCount.flow,
        observeSpendingCategoriesByDate.flow
    ) { loading, balance, transactions, itemCount, spending ->
        HomeScreenState(
            loading, balance, transactions, itemCount, spending
        )
    }

    fun init() {
        observeCurrentCashBalance(Unit)
        observeRecentTransactions(Unit)
        observeUserItemsCount(Unit)
        coroutineScope.launch {
            performInitialSync(Unit).collectInto(loader)
        }
        coroutineScope.launch {
            actions.collect { action ->
                when (action) {
                    is HomeScreenAction.SetSpendingWidgetDateRange -> {
                        spendingCategoryDateRange.emit(
                            ObserveSpendingCategoriesByDate.Params(
                                action.range
                            )
                        )
                    }
                    else -> {
                    }
                }
            }
        }
        coroutineScope.launch {
            spendingCategoryDateRange.collectLatest {
                val job = launch {
                    loader.addLoader()
                    observeSpendingCategoriesByDate(it)
                }
                job.invokeOnCompletion { loader.removeLoader() }
                job.join()
            }
        }
    }

    fun dispose() {
        coroutineScope.clear()
    }

    fun submit(action: HomeScreenAction) {
        coroutineScope.launch {
            actions.emit(action)
        }
    }
}

sealed class HomeScreenAction {
    object NavigateToPlaidLinkScreen : HomeScreenAction()
    object NavigateToTransactionsScreen : HomeScreenAction()
    data class SetSpendingWidgetDateRange(val range: RangeOption) :
        HomeScreenAction()
}

@Immutable
data class HomeScreenState(
    val loading: Boolean = false,
    val currentCashBalance: Double? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val userItemCount: Long? = null,
    val spendingByCategory: AllCategoriesSpend? = null
) {
    companion object {
        val Empty = HomeScreenState()
    }
}
