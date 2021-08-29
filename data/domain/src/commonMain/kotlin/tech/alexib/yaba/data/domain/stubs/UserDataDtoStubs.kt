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
package tech.alexib.yaba.data.domain.stubs

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.serialization.decodeFromString
import tech.alexib.yaba.data.domain.dto.AccountDto
import tech.alexib.yaba.data.domain.dto.AccountWithTransactionsDto
import tech.alexib.yaba.data.domain.dto.InstitutionDto
import tech.alexib.yaba.data.domain.dto.ItemDto
import tech.alexib.yaba.data.domain.dto.NewItemDto
import tech.alexib.yaba.data.domain.dto.TransactionDto
import tech.alexib.yaba.data.domain.dto.UserDataDto
import tech.alexib.yaba.data.domain.stubs.json.userJson
import tech.alexib.yaba.model.User
import tech.alexib.yaba.model.request.PlaidItemCreateRequest
import tech.alexib.yaba.model.request.UserLoginInput
import tech.alexib.yaba.model.response.AuthResponse
import tech.alexib.yaba.model.response.PlaidItemCreateResponse
import tech.alexib.yaba.util.jSerializer

object UserDataDtoStubs {

    val user: User by lazy { jSerializer.decodeFromString(userJson) }
    val items: List<ItemDto> by lazy { listOf(PlaidItemDtoStubs.chase) }
    val accounts: List<AccountDto> by lazy { AccountDtoStubs.chaseAccounts }
    val transactions: List<TransactionDto> by lazy {
        TransactionDtoStubs.transactionsChase1
    }
    val institutions: List<InstitutionDto> by lazy {
        listOf(InstitutionDtoStubs.chase)
    }

    val userData: UserDataDto by lazy {
        UserDataDto(
            user = user,
            items = items,
            accounts = accounts,
            transactions = transactions,
            institutions = institutions
        )
    }
    val groupedTransactions: Map<Uuid, List<TransactionDto>> by lazy {
        TransactionDtoStubs.allTransactions.groupBy { it.accountId }
    }

    val groupedAccounts: Map<Uuid, List<AccountDto>> by lazy {
        AccountDtoStubs.allAccounts.groupBy { it.itemId }
    }

    fun accountByIdWithTransactions(accountId: Uuid): AccountWithTransactionsDto? {
        return AccountDtoStubs.allAccounts.firstOrNull { it.id == accountId }?.let { accountDto ->
            AccountWithTransactionsDto(
                accountDto, groupedTransactions.getOrElse(accountId) { emptyList<TransactionDto>() }
            )
        }
    }

    val validLogin = UserLoginInput("alexi3@test.com", "testpassword")
    val goodAuthResponse = AuthResponse(
        email = validLogin.email,
        token = "authtoken",
        id = user.id
    )

    val newItemDtoStub: NewItemDto by lazy {
        val transactions: List<TransactionDto> =
            AccountDtoStubs.wellsFargoAccounts.flatMap { accountDto ->
                groupedTransactions.getOrElse(accountDto.id) { emptyList<TransactionDto>() }
            }
        NewItemDto(
            user = user,
            institutionDto = InstitutionDtoStubs.wellsFargo,
            item = PlaidItemDtoStubs.wellsFargo,
            accounts = AccountDtoStubs.wellsFargoAccounts,
            transactions = transactions
        )
    }

    object Registration {
        val newId = uuid4()
    }

    object PlaidLink {
        val item = PlaidItemDtoStubs.wellsFargo
        val institution = InstitutionDtoStubs.wellsFargo
        val publicToken = uuid4().toString()
        fun itemCreateResponse(): PlaidItemCreateResponse {

            val accounts: List<PlaidItemCreateResponse.Account> =
                AccountDtoStubs.wellsFargoAccounts.map {
                    PlaidItemCreateResponse.Account(
                        mask = it.mask, name = it.name, plaidAccountId = it.id.toString()

                    )
                }
            return PlaidItemCreateResponse(
                id = item.id,
                name = institution.name,
                logo = institution.logo,
                accounts = accounts
            )
        }

        val itemCreateRequest: PlaidItemCreateRequest by lazy {
            PlaidItemCreateRequest(
                institutionId = institution.institutionId,
                publicToken
            )
        }
    }
}
