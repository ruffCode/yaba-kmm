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
import kotlinx.serialization.decodeFromString
import tech.alexib.yaba.data.stubs.chaseStub
import tech.alexib.yaba.data.stubs.wellFargoStub
import tech.alexib.yaba.util.UuidSerializer
import tech.alexib.yaba.util.jSerializer

interface PlaidItemBase {
    val id: Uuid
    val plaidInstitutionId: String
    val name: String
    val base64Logo: String
}

@Serializable
data class PlaidItem(
    override val id: Uuid,
    override val plaidInstitutionId: String,
    override val name: String,
    override val base64Logo: String,
) : PlaidItemBase

@Serializable
data class PlaidItemWithAccounts(
    val plaidItem: PlaidItem,
    val accounts: List<Account>,
) : PlaidItemBase by plaidItem {
    val hiddenCount: Int by lazy {
        accounts.filter { it.hidden }.size
    }
}

object PlaidItemStubs {
    private val wellFargoWithAccounts: PlaidItemWithAccounts = jSerializer.decodeFromString(
        wellFargoStub
    )

    private val chaseWithAccounts: PlaidItemWithAccounts = jSerializer.decodeFromString(chaseStub)

    val itemsWithAccounts: List<PlaidItemWithAccounts> = listOf(
        wellFargoWithAccounts,
        chaseWithAccounts
    )
}
