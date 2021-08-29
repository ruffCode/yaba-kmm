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
package tech.alexib.yaba.android.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.data.repository.TransactionRepository
import tech.alexib.yaba.util.stateInDefault

class TransactionDetailScreenViewModel : ViewModel(), KoinComponent {

    //TODO refactor to use interactor
    private val repository: TransactionRepository by inject()

    private val transactionId = MutableStateFlow<Uuid?>(null)

    private val loadingFLow = MutableStateFlow(false)
    private val transactionDetailFlow = transactionId.flatMapLatest {
        loadingFLow.emit(true)
        if (it == null) emptyFlow() else repository.getById(it).also {
            loadingFLow.emit(false)
        }
    }

    val state: StateFlow<TransactionDetailScreenState> =
        combine(loadingFLow, transactionDetailFlow) { loading, transaction ->
            TransactionDetailScreenState(
                loading,
                transaction
            )
        }.stateInDefault(viewModelScope, TransactionDetailScreenState.Empty)

    fun getDetail(id: Uuid) {
        transactionId.value = id
    }
}
