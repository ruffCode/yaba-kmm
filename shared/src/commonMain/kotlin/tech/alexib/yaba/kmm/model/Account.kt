package tech.alexib.yaba.kmm.model

import com.benasher44.uuid.Uuid


data class Account(
    val id: Uuid,
    val name: String,
    val currentBalance: Double,
    val availableBalance: Double,
    val mask: String,
    val itemId: Uuid,
//    val institutionId: String,
    val type: AccountType,
    val subtype: AccountSubtype,
    val hidden: Boolean = false
) {
//   val logo = "https://ruffrevival.ngrok.io/logo/${institutionId}.png"
}

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
