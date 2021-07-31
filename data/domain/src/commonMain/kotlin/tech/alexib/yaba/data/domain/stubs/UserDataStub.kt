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

import kotlinx.serialization.decodeFromString
import tech.alexib.yaba.data.domain.dto.AccountDto
import tech.alexib.yaba.data.domain.dto.InstitutionDto
import tech.alexib.yaba.data.domain.dto.ItemDto
import tech.alexib.yaba.data.domain.dto.TransactionDto
import tech.alexib.yaba.data.domain.dto.UserDataDto
import tech.alexib.yaba.model.User
import tech.alexib.yaba.util.jSerializer

object UserDataStub {

    val user: User by lazy { jSerializer.decodeFromString(userJson) }
    val items: List<ItemDto> by lazy { jSerializer.decodeFromString(plaidItemsJson) }
    val accounts: List<AccountDto> by lazy { jSerializer.decodeFromString(accountsJson) }
    val transactions: List<TransactionDto> by lazy {
        jSerializer.decodeFromString(transactionsJson)
    }
    val institutions: List<InstitutionDto> by lazy {
        jSerializer.decodeFromString(institutionsJson)
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
}
