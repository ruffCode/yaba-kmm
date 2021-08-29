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
import tech.alexib.yaba.data.domain.stubs.json.accountsChaseJson
import tech.alexib.yaba.data.domain.stubs.json.accountsWellsJson
import tech.alexib.yaba.util.jSerializer

object AccountDtoStubs {

    val chaseAccounts: List<AccountDto> by lazy {
        jSerializer.decodeFromString(accountsChaseJson)
    }
    // ** 4 of 9 hidden
    val wellsFargoAccounts: List<AccountDto> by lazy {
        jSerializer.decodeFromString(accountsWellsJson)
    }

    // 18
    val allAccounts: List<AccountDto> by lazy {
        chaseAccounts + wellsFargoAccounts
    }
}
