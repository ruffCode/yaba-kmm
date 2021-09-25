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

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import tech.alexib.yaba.data.Immutable
import tech.alexib.yaba.data.observer.ObserveTransactions
import tech.alexib.yaba.data.util.Similarity.jaroWinklerSimilarity
import tech.alexib.yaba.data.util.SupervisorScope
import tech.alexib.yaba.model.Transaction

class TransactionsStore(
    private val observeTransactions: ObserveTransactions,
    dispatcher: CoroutineDispatcher
) {

    private val coroutineScope = SupervisorScope(dispatcher)

    private val actions = MutableSharedFlow<Action>()
    private val queryFlow = MutableStateFlow<String>("")
    private val searchingFlow = MutableStateFlow(queryFlow.value.isNotEmpty())

    val state: Flow<State> =
        combine(
            observeTransactions.flow,
            queryFlow,
            searchingFlow
        ) { transactions, query, searching ->
            State(
                transactions = transactions.filterByQuery(query),
                query = query,
                searching = searching
            )
        }

    fun init() {
        queryFlow.value = ""
        observeTransactions(ObserveTransactions.Params(null))
        coroutineScope.launch {
            actions.collect {
                when (it) {
                    is Action.SetQuery -> queryFlow.emit(it.query)
                    is Action.SetSearching -> searchingFlow.emit(!searchingFlow.value)
                    else -> {
                    }
                }
            }
        }
    }

    fun submit(action: Action) {
        coroutineScope.launch {
            actions.emit(action)
        }
    }

    private fun List<Transaction>.filterByQuery(query: String): List<Transaction> =
        if (query.isEmpty()) this
        else filter { transaction -> checkTransactionNameSimilarToQuery(transaction, query) }

    fun dispose() {
        coroutineScope.clear()
    }

    sealed class Action {
        data class SetQuery(val query: String) : Action()
        data class Select(val id: Uuid) : Action()
        object SetSearching : Action()
    }

    @Immutable
    data class State(
        val transactions: List<Transaction> = emptyList(),
        val query: String = "",
        val searching: Boolean = false
    ) {
        companion object {
            val Empty = State()
        }
    }

    private fun checkTransactionNameSimilarToQuery(transaction: Transaction, q: String): Boolean {
        val name = (transaction.merchantName ?: transaction.name).lowercase()
        val query = q.lowercase()

        @Suppress("MagicNumber")
        val cutOff = when (query.length) {
            in 0..2 -> 0.65
            in 3..5 -> 0.75
            in 6..8 -> 0.85
            else -> 0.9
        }

        return when (query) {
            name -> true
            else -> jaroWinklerSimilarity(name, query) >= cutOff
        }
    }
}
