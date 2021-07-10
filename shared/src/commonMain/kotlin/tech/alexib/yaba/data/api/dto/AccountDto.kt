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
package tech.alexib.yaba.data.api.dto

import com.benasher44.uuid.Uuid
import tech.alexib.yaba.data.db.AccountEntity
import tech.alexib.yaba.model.AccountSubtype
import tech.alexib.yaba.model.AccountType

internal data class AccountDto(
    val id: Uuid,
    val name: String,
    val currentBalance: Double,
    val availableBalance: Double,
    val mask: String,
    val itemId: Uuid,
    val type: AccountType,
    val subtype: AccountSubtype,
    val hidden: Boolean = false,
)

internal data class AccountWithTransactionsDto(
    val account: AccountDto,
    val transactions: List<TransactionDto>,
)

internal fun tech.alexib.yaba.fragment.Account.toDto(): AccountDto = AccountDto(
    id = id as Uuid,
    name = name,
    mask = mask,
    availableBalance = availableBalance,
    currentBalance = currentBalance,
    itemId = itemId as Uuid,
    type = AccountType.valueOf(type.name),
    subtype = AccountSubtype.valueOf(subtype.name),
    hidden = hidden
)

internal fun AccountDto.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    current_balance = currentBalance,
    available_balance = availableBalance,
    mask = mask,
    item_id = itemId,
    type = type,
    subtype = subtype,
    hidden = hidden
)

internal fun List<AccountDto>.toEntities(): List<AccountEntity> = this.map { it.toEntity() }

internal fun tech.alexib.yaba.fragment.AccountWithTransactions.toAccountWithTransactions() =
    AccountWithTransactionsDto(
        account = AccountDto(
            id = id as Uuid,
            name = name,
            mask = mask,
            availableBalance = availableBalance,
            currentBalance = currentBalance,
            itemId = itemId as Uuid,
            type = AccountType.valueOf(type.name),
            subtype = AccountSubtype.valueOf(subtype.name),
            hidden = hidden
        ),
        transactions = transactions.map { it.fragments.transaction.toDto() }
    )
