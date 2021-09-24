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

import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.Contextual
import tech.alexib.yaba.data.Immutable
import tech.alexib.yaba.data.Parcelable
import tech.alexib.yaba.data.Parcelize
import tech.alexib.yaba.data.interactor.AddItem
import tech.alexib.yaba.data.interactor.SetAccountsToHide
import tech.alexib.yaba.data.util.SupervisorScope
import tech.alexib.yaba.model.defaultLogoBase64
import tech.alexib.yaba.util.InvokeError
import tech.alexib.yaba.util.InvokeStarted
import tech.alexib.yaba.util.InvokeSuccess
import tech.alexib.yaba.util.ObservableLoadingCounter

class PlaidLinkResultStore(
    private val addItem: AddItem,
    private val setAccountsToHide: SetAccountsToHide,
    private val log: Kermit,
    dispatcher: CoroutineDispatcher,
) {
    private val coroutineScope = SupervisorScope(dispatcher)
    private val loader = ObservableLoadingCounter()
    private val accountsFlow = MutableStateFlow<List<PlaidLinkScreenResult.Account>>(emptyList())
    private val shouldNavigateHomeFlow = MutableStateFlow(false)
    private lateinit var itemId: Uuid
    private val actions = MutableSharedFlow<Action>()
    val state = combine(
        loader.observable,
        accountsFlow,
        shouldNavigateHomeFlow
    ) { loading, accounts, shouldNavigate ->
        PlaidLinkResultScreenState(loading, shouldNavigate, accounts)
    }

    fun init(itemId: Uuid, accounts: List<PlaidLinkScreenResult.Account>) {
        this.itemId = itemId
        accountsFlow.value = accounts
    }

    fun submit(action: Action) {
        coroutineScope.launch {
            actions.emit(action)
        }
    }

    init {
        coroutineScope.launch {
            actions.collectLatest { action ->
                when (action) {
                    is Action.SetAccountShown -> {
                        accountsFlow.emit(getUpdatedAccounts(action.plaidAccountId, action.show))
                    }
                    is Action.Submit -> {
                        val accountsToHide = accountsFlow.value
                            .filter { !it.show }
                            .map { it.plaidAccountId }
                        setAccountsToHide(
                            SetAccountsToHide.Params(itemId, accountsToHide)
                        ).collect {
                            when (it) {
                                is InvokeStarted -> loader.addLoader()
                                is InvokeSuccess -> {
                                    val job = launch {
                                        addItem.executeSync(AddItem.Params(itemId))
                                    }
                                    job.invokeOnCompletion {
                                        loader.removeLoader()
                                    }
                                    job.join()
                                    shouldNavigateHomeFlow.emit(true)
                                }
                                is InvokeError -> {
                                    log.e(it.throwable) { "Error loading transactions" }
                                    loader.removeLoader()
                                    shouldNavigateHomeFlow.emit(true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getUpdatedAccounts(
        plaidAccountId: String,
        show: Boolean
    ): List<PlaidLinkScreenResult.Account> {
        val currentAccounts = accountsFlow.value.toMutableList()
        val currentItem = currentAccounts.first { it.plaidAccountId == plaidAccountId }
        currentAccounts[currentAccounts.indexOf(currentItem)] = currentItem.copy(show = show)
        return currentAccounts
    }

    fun dispose() {
        coroutineScope.clear()
    }

    sealed class Action {
        object Submit : Action()
        data class SetAccountShown(val plaidAccountId: String, val show: Boolean) :
            Action()
    }
}

@Immutable
@Parcelize
data class PlaidLinkScreenResult(
    @Contextual
    val id: Uuid,
    val name: String,
    val logo: String = defaultLogoBase64,
    val accounts: List<Account>
) : Parcelable {
    @Immutable
    @Parcelize
    data class Account(
        val mask: String,
        val name: String,
        val plaidAccountId: String,
        var show: Boolean = true
    ) : Parcelable
}

@Immutable
data class PlaidLinkResultScreenState(
    val loading: Boolean = false,
    val shouldNavigateHome: Boolean = false,
    val accounts: List<PlaidLinkScreenResult.Account> = emptyList()
) {
    companion object {
        val Empty = PlaidLinkResultScreenState()
    }
}
