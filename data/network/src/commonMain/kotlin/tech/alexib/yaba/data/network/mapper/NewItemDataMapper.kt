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
package tech.alexib.yaba.data.network.mapper

import tech.alexib.yaba.data.domain.dto.AccountDto
import tech.alexib.yaba.data.domain.dto.NewItemDto
import tech.alexib.yaba.data.domain.dto.TransactionDto
import tech.alexib.yaba.model.User

internal fun yaba.schema.NewItemDataQuery.Data.toDto(): NewItemDto = this.run {
    val item = itemById.itemWithInstitution
    val accountTransactionPair = itemById.accountsAndTransactions()
    NewItemDto(
        item = item.toDto(me.id),
        accounts = accountTransactionPair.first,
        transactions = accountTransactionPair.second,
        user = User(me.id, me.email),
        institutionDto = item.institution.institution.toDto()
    )
}

internal fun yaba.schema.NewItemDataQuery.ItemById.accountsAndTransactions():
    Pair<List<AccountDto>, List<TransactionDto>> {
    return Pair(
        this.accounts.map { it.accountWithTransactions.account.toDto() },
        this.accounts.flatMap {
            it.accountWithTransactions.transactions.map { t ->
                t.transaction.toDto()
            }
        }
    )
}
