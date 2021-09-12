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
package tech.alexib.yaba.stubs

import kotlinx.serialization.decodeFromString
import tech.alexib.yaba.model.Transaction
import tech.alexib.yaba.model.TransactionDetail
import tech.alexib.yaba.stubs.json.transactionsChaseJson1
import tech.alexib.yaba.stubs.json.transactionsWellsJson1
import tech.alexib.yaba.util.jSerializer

object TransactionStubs {

    val transactionsChase1: List<Transaction> by lazy {
        jSerializer.decodeFromString(transactionsChaseJson1)
    }

    val transactionsWellsFargo1: List<Transaction> by lazy {
        jSerializer.decodeFromString(transactionsWellsJson1)
    }

    val transactionDetail by lazy {
        transactionsWellsFargo1.map {
            val account = AccountStubs.wellsFargoAccounts.first { a -> a.id == it.accountId }
            with(it) {
                TransactionDetail(
                    id = id,
                    accountId = accountId,
                    name = name,
                    type = type,
                    amount = amount,
                    date = date,
                    category = category,
                    subcategory = subcategory,
                    isoCurrencyCode = isoCurrencyCode,
                    accountMask = account.mask,
                    accountName = account.name,
                    institutionName = "Wells Fargo",
                    pending = pending
                )
            }
        }
    }
}
