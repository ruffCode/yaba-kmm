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
import kotlinx.coroutines.flow.combine
import tech.alexib.yaba.data.Immutable
import tech.alexib.yaba.data.observer.ObserveAccount
import tech.alexib.yaba.data.observer.ObserveAccountTransactions
import tech.alexib.yaba.data.observer.ObserveItem
import tech.alexib.yaba.model.Account
import tech.alexib.yaba.model.PlaidItem
import tech.alexib.yaba.model.Transaction

class AccountDetailStore(
    private val observeAccountTransactions: ObserveAccountTransactions,
    private val observeItem: ObserveItem,
    private val observeAccount: ObserveAccount,
) {

    val state = combine(
        observeAccount.flow,
        observeItem.flow,
        observeAccountTransactions.flow
    ) { account, item, transactions ->
        AccountDetailScreenState(false, item, account, transactions)
    }

    fun init(accountId: Uuid, itemId: Uuid) {
        observeAccount(ObserveAccount.Params(accountId))
        observeItem(ObserveItem.Params(itemId))
        observeAccountTransactions(ObserveAccountTransactions.Params(accountId))
    }
}

@Immutable
data class AccountDetailScreenState(
    val loading: Boolean = false,
    val plaidItem: PlaidItem? = null,
    val account: Account? = null,
    val transactions: List<Transaction> = emptyList()
) {
    companion object {
        val Empty = AccountDetailScreenState()
    }
}
