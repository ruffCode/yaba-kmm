package tech.alexib.yaba.kmm.model

import com.benasher44.uuid.Uuid


data class Account(
    val id: Uuid,
    val name: String,
    val currentBalance: Double,
    val availableBalance: Double,
    val mask: String,
    val itemId: Uuid,
    val type: AccountType,
    val subtype: AccountSubtype,
    val hidden: Boolean = false
)

enum class AccountType {
    DEPOSITORY,
    CREDIT,
    INVESTMENT,
    LOAN
}


enum class AccountSubtype {
    CHECKING,
    SAVINGS,
    CD,
    CREDIT_CARD,
    MONEY_MARKET,
    IRA,
    FOUR_HUNDRED_ONE_K,
    STUDENT,
    MORTGAGE
}
