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
package tech.alexib.yaba.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.repository.AccountRepository
import tech.alexib.yaba.data.repository.ItemRepository
import tech.alexib.yaba.data.repository.TransactionRepository
import tech.alexib.yaba.data.store.HomeScreenState
import tech.alexib.yaba.data.store.HomeStore
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.util.stateInDefault

// !!TODO Placeholder/early prototype - hoping to extract this type of logic
private class HomeDataLoader(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository,
    itemRepository: ItemRepository
) {
    private val recentTransactionsParam = MutableStateFlow<Unit?>(null)
    private val currentBalanceParam = MutableStateFlow<Unit?>(null)

    private val recentTransactionFlow: Flow<List<Transaction>> =
        recentTransactionsParam.flatMapLatest {
            if (it == null) emptyFlow()
            else transactionRepository.recentTransactions()
        }

    private val currentBalanceFlow: Flow<Double> = currentBalanceParam.flatMapLatest {
        if (it == null) emptyFlow()
        else accountRepository.currentCashBalance()
    }
    private val userItemCountFlow: Flow<Long> = currentBalanceParam.flatMapLatest {
        if (it == null) emptyFlow()
        else itemRepository.userItemsCount()
    }

    fun observeRecentTransactions(): Flow<List<Transaction>> {
        return recentTransactionFlow
    }

    fun observeCurrentBalance(): Flow<Double> {
        return currentBalanceFlow
    }

    fun observeUserItemCount(): Flow<Long> {
        return userItemCountFlow
    }

    operator fun invoke() {
        recentTransactionsParam.value = Unit
        currentBalanceParam.value = Unit
    }
}

class HomeViewModel(
    private val homeStore: HomeStore
) : ViewModel(), KoinComponent {


    //    private val homeDataLoaderState = ObservableLoadingCounter()
//    private val accountRepository: AccountRepository by inject()
//    private val transactionRepository: TransactionRepository by inject()
//    private val itemRepository: ItemRepository by inject()
    private val log: Kermit by inject { parametersOf("HomeViewModel") }
    private val scope = viewModelScope

    //    private val homeDataLoader =
//        HomeDataLoader(accountRepository, transactionRepository, itemRepository)
//
//    val state: Flow<HomeScreenState> =
//        combine(
//            homeDataLoaderState.observable,
//            homeDataLoader.observeCurrentBalance().distinctUntilChanged(),
//            homeDataLoader.observeRecentTransactions().distinctUntilChanged(),
//            homeDataLoader.observeUserItemCount().distinctUntilChanged(),
//        ) { loadingState, cashBalance, recentTransactions, userItemCount ->
//            HomeScreenState(loadingState, cashBalance, recentTransactions, userItemCount)
//        }
    val state = homeStore.state.stateInDefault(viewModelScope, HomeScreenState.Empty)

    init {
        homeStore.init(viewModelScope)
        Firebase.messaging.isAutoInitEnabled = true
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
//        homeDataLoader()
//        scope.launch {
//            initializer.init().collectInto(homeDataLoaderState)
//        }
    }
}
