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
@file:UseSerializers(UuidSerializer::class)

package tech.alexib.yaba.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.util.UuidSerializer

@Serializable
data class Account(
    val id: Uuid,
    val name: String,
    val currentBalance: Double,
    val availableBalance: Double,
    val mask: String,
    val itemId: Uuid,
    val type: AccountType,
    val subtype: AccountSubtype,
    val hidden: Boolean = false,
    val institutionName: String
)

fun List<Account>.availableCashBalance() =
    this.filter { it.type == AccountType.DEPOSITORY }.sumOf { it.availableBalance }

fun List<Account>.currentCashBalance() =
    this.filter { it.type == AccountType.DEPOSITORY }.sumOf { it.currentBalance }

@Serializable
enum class AccountType {
    DEPOSITORY,
    CREDIT,
    INVESTMENT,
    LOAN
}

@Serializable
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

object AccountStubs {
    val checking = Account(
        id = uuidFrom("dd3ab84b-372e-4fec-b61b-ac4084dd4b29"),
        name = "Plaid Checking",
        currentBalance = 110.0,
        availableBalance = 100.0,
        mask = "0000",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.DEPOSITORY,
        subtype = AccountSubtype.CHECKING,
        hidden = false,
        institutionName = "Chase"
    )
    val savings = Account(
        id = uuidFrom("92736233-c812-4b07-97b7-f9727f20e390"),
        name = "Plaid Saving",
        currentBalance = 210.0,
        availableBalance = 200.0,
        mask = "1111",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.DEPOSITORY,
        subtype = AccountSubtype.SAVINGS,
        hidden = false,
        institutionName = "Chase"
    )
    val cd = Account(
        id = uuidFrom("9fbbe19f-0da1-4d7e-987a-4159003d8e76"),
        name = "Plaid CD",
        currentBalance = 1000.0,
        availableBalance = 0.0,
        mask = "2222",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.DEPOSITORY,
        subtype = AccountSubtype.CD,
        hidden = false,
        institutionName = "Chase"
    )
    val creditCard = Account(
        id = uuidFrom("a7d03ff7-4f5b-4b8c-a974-9ada6b1965ee"),
        name = "Plaid Credit Card",
        currentBalance = 410.0,
        availableBalance = 0.0,
        mask = "3333",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.CREDIT,
        subtype = AccountSubtype.CREDIT_CARD,
        hidden = false,
        institutionName = "Chase"
    )
    val moneyMarket = Account(
        id = uuidFrom("d7ff47ba-4bd4-4d26-98f7-024eb6035ab0"),
        name = "Plaid Money Market",
        currentBalance = 43200.0,
        availableBalance = 43200.0,
        mask = "4444",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.DEPOSITORY,
        subtype = AccountSubtype.MONEY_MARKET,
        hidden = false,
        institutionName = "Chase"
    )
    val ira = Account(
        id = uuidFrom("de6cd453-9e63-44ec-bcb5-d36f1371bbd5"),
        name = "Plaid IRA",
        currentBalance = 320.76,
        availableBalance = 0.0,
        mask = "5555",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.INVESTMENT,
        subtype = AccountSubtype.IRA,
        hidden = false,
        institutionName = "Chase"
    )
    val FOUR_HUNDRED_ONE_K = Account(
        id = uuidFrom("14d41f78-47ab-4f03-b8eb-e3ee5db00dc0"),
        name = "Plaid 401k",
        currentBalance = 23631.9805,
        availableBalance = 0.0,
        mask = "6666",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.INVESTMENT,
        subtype = AccountSubtype.FOUR_HUNDRED_ONE_K,
        hidden = false,
        institutionName = "Chase"
    )

    val studentLoan = Account(
        id = uuidFrom("4652339a-245a-4874-8212-520859c26d03"),
        name = "Plaid Student Loan",
        currentBalance = 65262.0,
        availableBalance = 0.0,
        mask = "7777",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.LOAN,
        subtype = AccountSubtype.STUDENT,
        hidden = false,
        institutionName = "Chase"
    )

    val mortgage = Account(
        id = uuidFrom("8765aa57-a2f7-42e4-8ec1-b1b6c3d03ef2"),
        name = "Plaid Mortgage",
        currentBalance = 56302.06,
        availableBalance = 0.0,
        mask = "8888",
        itemId = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        type = AccountType.LOAN,
        subtype = AccountSubtype.MORTGAGE,
        hidden = false,
        institutionName = "Chase"
    )

    val accounts = listOf(
        checking, savings, cd, creditCard, moneyMarket, ira, FOUR_HUNDRED_ONE_K,
        studentLoan, mortgage
    )
}
