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
import tech.alexib.yaba.data.domain.dto.TransactionDto
import tech.alexib.yaba.data.domain.stubs.json.transactionsChaseJson1
import tech.alexib.yaba.data.domain.stubs.json.transactionsChaseJson2
import tech.alexib.yaba.data.domain.stubs.json.transactionsChaseJson3
import tech.alexib.yaba.data.domain.stubs.json.transactionsWellsJson1
import tech.alexib.yaba.data.domain.stubs.json.transactionsWellsJson2
import tech.alexib.yaba.util.jSerializer

object TransactionDtoStubs {
    // 81
    val transactionsChase1: List<TransactionDto> by lazy {
        jSerializer.decodeFromString(transactionsChaseJson1)
    }
    // 46
    val transactionsChase2: List<TransactionDto> by lazy {
        jSerializer.decodeFromString(transactionsChaseJson2)
    }
    // 66
    val transactionsChase3: List<TransactionDto> by lazy {
        jSerializer.decodeFromString(transactionsChaseJson3)
    }
    // 85
    val transactionsWellsFargo1: List<TransactionDto> by lazy {
        jSerializer.decodeFromString(transactionsWellsJson1)
    }
    // 62
    val transactionsWellsFargo2: List<TransactionDto> by lazy {
        jSerializer.decodeFromString(transactionsWellsJson2)
    }
    // 193
    val allChaseTransactions by lazy {
        transactionsChase1 + transactionsChase2 + transactionsChase3
    }
    // 147
    val allWellsTransactions by lazy {
        transactionsWellsFargo1 + transactionsWellsFargo2
    }
    // 340
    val allTransactions by lazy {
        allChaseTransactions + allWellsTransactions
    }
}
