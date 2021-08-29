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
import tech.alexib.yaba.data.domain.dto.AccountWithTransactionsDto
import tech.alexib.yaba.model.AccountSubtype
import tech.alexib.yaba.model.AccountType

internal fun yaba.schema.fragment.Account.toDto(): AccountDto = AccountDto(
    id = id,
    name = name,
    mask = mask,
    availableBalance = availableBalance,
    currentBalance = currentBalance,
    creditLimit = creditLimit,
    itemId = itemId,
    type = AccountType.valueOf(type.rawValue),
    subtype = AccountSubtype.valueOf(subtype.rawValue),
    hidden = hidden
)

internal fun yaba.schema.fragment.AccountWithTransactions.toAccountWithTransactions() =
    AccountWithTransactionsDto(
        account = this.fragments.account.toDto(),
        transactions = transactions.map { it.fragments.transaction.toDto() }
    )
