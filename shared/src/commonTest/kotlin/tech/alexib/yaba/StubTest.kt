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
package tech.alexib.yaba

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import tech.alexib.yaba.data.stubs.wellFargoStub
import tech.alexib.yaba.model.PlaidItemWithAccounts
import tech.alexib.yaba.util.jSerializer
import kotlin.test.Test

class StubTest {

    @Test
    fun parsesPlaidItemWithAccountsStub() {
        try {

            val itemWithAccounts: PlaidItemWithAccounts = jSerializer.decodeFromString(wellFargoStub)
            assert(itemWithAccounts.name == "Wells Fargo")
        } catch (e: Throwable) {
            when (e) {
                is SerializationException -> {
                    println(e.printStackTrace())
                }
            }
            println(e.message)
            throw e
        }
    }
}
