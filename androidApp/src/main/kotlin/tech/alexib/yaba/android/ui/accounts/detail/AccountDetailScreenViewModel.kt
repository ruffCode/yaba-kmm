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
package tech.alexib.yaba.android.ui.accounts.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.repository.AccountRepository
import tech.alexib.yaba.data.repository.ItemRepository
import tech.alexib.yaba.data.repository.TransactionRepository
import tech.alexib.yaba.model.Account
import tech.alexib.yaba.model.PlaidItem
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.util.stateInDefault

class AccountDetailScreenViewModel : ViewModel(), KoinComponent {
    //TODO refactor to use interactor
    private val loadingFlow = MutableStateFlow(false)
    private val accountRepository: AccountRepository by inject()
    private val transactionRepository: TransactionRepository by inject()
    private val itemRepository: ItemRepository by inject()
    private val log: Kermit by inject { parametersOf("AccountDetailScreenViewModel") }
    private val scope = viewModelScope
    private val dataLoader =
        AccountDetailDataLoader(accountRepository, transactionRepository, itemRepository)

    val state: StateFlow<AccountDetailScreenState> =
        combine(
            loadingFlow,
            dataLoader.observeItem().distinctUntilChanged(),
            dataLoader.observeAccount().distinctUntilChanged(),
            dataLoader.observeTransactions().distinctUntilChanged()
        ) { loading, item, account, transactions ->
            AccountDetailScreenState(
                loading,
                item,
                account,
                transactions
            )
        }.stateInDefault(
            scope,
            initialValue = AccountDetailScreenState.Empty
        )

    fun init(accountId: Uuid, itemId: Uuid) {
        dataLoader(accountId, itemId)
    }
}

private class AccountDetailDataLoader(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository,
    itemRepository: ItemRepository
) {
    private val accountIdParam = MutableStateFlow<Uuid?>(null)
    private val itemIdParam = MutableStateFlow<Uuid?>(null)

    private val itemFlow: Flow<PlaidItem> =
        itemIdParam.flatMapLatest {
            if (it == null) emptyFlow()
            else itemRepository.getById(it).filterNotNull()
        }
    private val accountFlow: Flow<Account> =
        accountIdParam.flatMapLatest {
            if (it == null) emptyFlow()
            else accountRepository.getById(it).filterNotNull()
        }
    private val transactionsFlow: Flow<List<Transaction>> =
        accountIdParam.flatMapLatest {
            if (it == null) emptyFlow()
            else transactionRepository.getAllByAccountId(it)
        }

    fun observeTransactions(): Flow<List<Transaction>> {
        return transactionsFlow
    }

    fun observeAccount(): Flow<Account> {
        return accountFlow
    }

    fun observeItem(): Flow<PlaidItem> {
        return itemFlow
    }

    operator fun invoke(accountId: Uuid, itemId: Uuid) {
        accountIdParam.value = accountId
        itemIdParam.value = itemId
    }
}
