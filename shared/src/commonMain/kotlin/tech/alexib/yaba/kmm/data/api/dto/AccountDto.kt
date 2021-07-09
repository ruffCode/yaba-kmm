package tech.alexib.yaba.kmm.data.api.dto

import com.benasher44.uuid.Uuid
import tech.alexib.yaba.data.db.AccountEntity
import tech.alexib.yaba.kmm.model.AccountSubtype
import tech.alexib.yaba.kmm.model.AccountType

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
