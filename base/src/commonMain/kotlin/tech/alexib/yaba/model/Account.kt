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
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.util.UuidSerializer

@Serializable
data class Account(
    val id: Uuid,
    val name: String,
    val currentBalance: Double,
    val availableBalance: Double? = null,
    val creditLimit: Double? = null,
    val mask: String,
    val itemId: Uuid,
    val type: AccountType,
    val subtype: AccountSubtype,
    val hidden: Boolean = false,
    val institutionName: String
)

fun List<Account>.availableCashBalance() =
    this.filter { it.type == AccountType.DEPOSITORY && it.availableBalance != null }
        .sumOf { it.availableBalance ?: 0.0 }

fun List<Account>.currentCashBalance() =
    this.filter { it.type == AccountType.DEPOSITORY }.sumOf { it.currentBalance }

@Serializable
enum class AccountType {
    DEPOSITORY,
    CREDIT,
    INVESTMENT,
    LOAN,
    OTHER
}

@Serializable
enum class AccountSubtype(val text: String) {
//    CHECKING("Checking"),
//    SAVINGS("Savings"),
//    CD("CD"),
//    CREDIT_CARD("Credit card"),
//    MONEY_MARKET("Money market"),
//    IRA("IRA"),
//    FOUR_HUNDRED_ONE_K("401k"),
//    STUDENT("Student Loan"),
//    MORTGAGE("Mortgage"),
    FOUR_HUNDRED_ONE_A("401a"),
    FOUR_HUNDRED_ONE_K("401k"),
    FOUR_HUNDRED_THREE_B("403B"),
    FOUR_HUNDRED_FIFTY_SEVEN_B("457b"),
    FIVE_HUNDRED_TWENTY_NINE("529"),
    BROKERAGE("Brokerage"),
    CASH_ISA("Cash ISA"),
    EDUCATION_SAVINGS_ACCOUNT("education savings account"),
    GIC("gic"),
    HEALTH_REIMBURSEMENT_ARRANGEMENT("health reimbursement arrangement"),
    HSA("hsa"),
    ISA("isa"),
    IRA("ira"),
    LIF("lif"),
    LIRA("lira"),
    LRIF("lrif"),
    LRSP("lrsp"),
    NON_TAXABLE_BROKERAGE_ACCOUNT("non-taxable brokerage account"),
    OTHER("other"),
    PRIF("prif"),
    RDSP("rdsp"),
    RESP("resp"),
    RLIF("rlif"),
    RRIF("rrif"),
    PENSION("pension"),
    PROFIT_SHARING_PLAN("profit sharing plan"),
    RETIREMENT("retirement"),
    ROTH("roth"),
    ROTH_401_K("roth 401k"),
    RRSP("rrsp"),
    SEP_IRA("sep ira"),
    SIMPLE_IRA("simple ira"),
    SIPP("sipp"),
    STOCK_PLAN("stock plan"),
    THRIFT_SAVINGS_PLAN("thrift savings plan"),
    TFSA("tfsa"),
    TRUST("trust"),
    UGMA("ugma"),
    UTMA("utma"),
    VARIABLE_ANNUITY("variable annuity"),
    CREDIT_CARD("credit card"),
    PAYPAL("paypal"),
    CD("cd"),
    CHECKING("checking"),
    SAVINGS("savings"),
    MONEY_MARKET("money market"),
    PREPAID("prepaid"),
    AUTO("auto"),
    COMMERCIAL("commercial"),
    CONSTRUCTION("construction"),
    CONSUMER("consumer"),
    HOME("home"),
    HOME_EQUITY("home equity"),
    LOAN("loan"),
    MORTGAGE("mortgage"),
    OVERDRAFT("overdraft"),
    LINE_OF_CREDIT("line of credit"),
    STUDENT("student"),
    CASH_MANAGEMENT("cash management"),
    KEOGH("keogh"),
    MUTUAL_FUND("mutual fund"),
    RECURRING("recurring"),
    REWARDS("rewards"),
    SAFE_DEPOSIT("safe deposit"),
    SARSEP("sarsep")
}

// TODO think about implementing this
// sealed class AccountModel {
//    abstract val id: Uuid
//    abstract val name: String
//    abstract val currentBalance: Double
//    abstract val mask: String
//    abstract val itemId: Uuid
//    abstract val hidden: Boolean
//    abstract val institutionName: String
// }
//
// data class DepositoryAccount(
//    override val id: Uuid,
//    override val name: String,
//    override val currentBalance: Double,
//    val availableBalance: Double? = null,
//    override val mask: String,
//    override val itemId: Uuid,
//    override val hidden: Boolean = false,
//    override val institutionName: String,
//    val type: Type
// ) : AccountModel() {
//    enum class Type {
//        CHECKING,
//        SAVINGS,
//        MONEY_MARKET,
//        CD
//    }
// }
//
// data class CreditCardAccount(
//    override val id: Uuid,
//    override val name: String,
//    override val currentBalance: Double,
//    val limit: Double,
//    override val mask: String,
//    override val itemId: Uuid,
//    override val hidden: Boolean = false,
//    override val institutionName: String,
// ) : AccountModel()
//
// data class InvestmentAccount(
//    override val id: Uuid,
//    override val name: String,
//    override val currentBalance: Double,
//    override val mask: String,
//    override val itemId: Uuid,
//    override val hidden: Boolean = false,
//    override val institutionName: String,
//    val type: Type
// ) : AccountModel() {
//    enum class Type {
//        IRA,
//        FOUR_HUNDRED_ONE_K,
//    }
// }
//
// data class LoanAccount(
//    override val id: Uuid,
//    override val name: String,
//    override val currentBalance: Double,
//    override val mask: String,
//    override val itemId: Uuid,
//    override val hidden: Boolean = false,
//    override val institutionName: String,
//    val type: Type
// ) : AccountModel() {
//    enum class Type {
//        STUDENT,
//        MORTGAGE
//    }
// }
//
// fun Account.toModel(): AccountModel = when (this.type) {
//    AccountType.DEPOSITORY -> DepositoryAccount(
//        id = id,
//        name = name,
//        currentBalance = currentBalance,
//        availableBalance = availableBalance,
//        mask = mask,
//        itemId = itemId,
//        hidden = hidden,
//        institutionName = institutionName,
//        type = DepositoryAccount.Type.valueOf(subtype.name)
//    )
//    AccountType.LOAN -> LoanAccount(
//        id = id,
//        name = name,
//        currentBalance = currentBalance,
//        mask = mask,
//        itemId = itemId,
//        hidden = hidden,
//        institutionName = institutionName,
//        type = LoanAccount.Type.valueOf(subtype.name)
//    )
//    AccountType.INVESTMENT -> InvestmentAccount(
//        id = id,
//        name = name,
//        currentBalance = currentBalance,
//        mask = mask,
//        itemId = itemId,
//        hidden = hidden,
//        institutionName = institutionName,
//        type = InvestmentAccount.Type.valueOf(subtype.name)
//    )
//    AccountType.CREDIT -> CreditCardAccount(
//        id = id,
//        name = name,
//        currentBalance = currentBalance,
//        mask = mask,
//        itemId = itemId,
//        hidden = hidden,
//        institutionName = institutionName,
//        limit = creditLimit ?: 0.0
//    )
// }
