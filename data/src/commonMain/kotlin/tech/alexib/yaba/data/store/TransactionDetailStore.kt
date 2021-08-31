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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import tech.alexib.yaba.data.Immutable
import tech.alexib.yaba.data.observer.ObserveTransactionDetail
import tech.alexib.yaba.model.TransactionDetail

class TransactionDetailStore(
    private val observeTransactionDetail: ObserveTransactionDetail,
) {

    val state: Flow<TransactionDetailScreenState> = observeTransactionDetail
        .flow
        .mapLatest { TransactionDetailScreenState(false, it) }

    fun init(transactionId: Uuid) {
        observeTransactionDetail(transactionId)
    }
}

@Immutable
data class TransactionDetailScreenState(
    val loading: Boolean = false,
    val transaction: TransactionDetail? = null
) {
    companion object {
        val Empty = TransactionDetailScreenState()
    }
}
