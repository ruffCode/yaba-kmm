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
package tech.alexib.yaba.data.domain

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Kermit
import tech.alexib.yaba.data.domain.stubs.AccountDtoStubs
import tech.alexib.yaba.data.domain.stubs.InstitutionDtoStubs
import tech.alexib.yaba.data.domain.stubs.PlaidItemDtoStubs
import tech.alexib.yaba.data.domain.stubs.TransactionDtoStubs
import tech.alexib.yaba.data.domain.stubs.UserDataDtoStubs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StubTests {
    private val log = Kermit(CommonLogger())

    @Test
    fun parsesJson() {
        runCatching {

            val user = UserDataDtoStubs.user
            assertEquals("alexi3@test.com", user.email)

            val userData = UserDataDtoStubs.userData
            assertTrue(userData.transactions.isNotEmpty())

            assertEquals(340, TransactionDtoStubs.allTransactions.size)

            assertEquals(18, AccountDtoStubs.allAccounts.size)

            assertEquals("Wells Fargo", InstitutionDtoStubs.wellsFargo.name)
            assertEquals("ins_4", PlaidItemDtoStubs.wellsFargo.plaidInstitutionId)
        }.getOrThrow()
    }
}
