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
package tech.alexib.yaba.kmm.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.benasher44.uuid.uuidFrom
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import tech.alexib.yaba.kmm.util.serializer

@Serializable
enum class TransactionType {
    DIGITAL,
    PLACE,
    SPECIAL,
    UNRESOLVED
}

data class Transaction(
    val id: Uuid,
    val accountId: Uuid,
    val name: String,
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val category: String?,
    val subcategory: String?,
    val isoCurrencyCode: String?,
    val merchantName: String? = null,
    val pending: Boolean = false
)

@Serializable
data class TransactionDetail(
    @Contextual
    val id: Uuid,
    @Contextual
    val accountId: Uuid,
    val name: String,
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val category: String?,
    val subcategory: String?,
    val isoCurrencyCode: String?,
    val pending: Boolean? = false,
    val merchantName: String? = null,
    val institutionName: String,
    val accountName: String,
    val accountMask: String,
) {
    @Transient
    val label = "$institutionName $accountName ****$accountMask"
}

object TransactionStubs {

    val transactionStub = Transaction(
        name = "ACH Electronic CreditGUSTO PAY 123456",
        id = uuidFrom("c8833368-75f7-4019-98d0-83b680b5dd8d"),
        type = TransactionType.SPECIAL,
        amount = 500.0,
        date = LocalDate.parse("2021-04-25"),
        accountId = uuidFrom("6e49eb05-af5f-4f2e-9d9d-6d98117a602f"),
//    itemId = uuidFrom("99dd6382-0b02-46c5-aaa4-ada87c505443"),
        category = "Travel",
        subcategory = "Airlines and Aviation Services",
        isoCurrencyCode = "USD",
        pending = true,
    )

    val transactions = listOf(
        transactionStub, transactionStub,
        transactionStub, transactionStub, transactionStub, transactionStub, transactionStub,
        transactionStub, transactionStub, transactionStub, transactionStub, transactionStub,
        transactionStub, transactionStub, transactionStub, transactionStub, transactionStub,
        transactionStub
    )

// s [isoCurrencyCode, institutionName, accountName, accountMask
    val detailStub = """
        {
        "id":"${uuid4()}",
        "accountId":"${uuid4()}",
            "account_id": "MkLNaqkNJASB3anarQwwUx8XxjBQ7puDjnZVp",
            "account_owner": null,
            "amount": 6.33,
            "authorized_date": null,
            "authorized_datetime": null,
            "category":"Travel",
            "subcategory": "Taxi",
            "category_id": "22016000",
            "date": "2021-06-22",
            "datetime": null,
            "isoCurrencyCode": "USD",
            "location": {
                "address": null,
                "city": null,
                "country": null,
                "lat": null,
                "lon": null,
                "postal_code": null,
                "region": null,
                "store_number": null
            },
            "institutionName":"Chase",
            "accountName":"Checking",
            "merchantName": "Uber",
            "name": "Uber 072515 SF**POOL**",
            "payment_channel": "in store",
            "accountMask":"0000",
            "payment_meta": {
                "by_order_of": null,
                "payee": null,
                "payer": null,
                "payment_method": null,
                "payment_processor": null,
                "ppd_id": null,
                "reason": null,
                "reference_number": null
            },
            "pending": false,
            "pending_transaction_id": null,
            "transaction_code": null,
            "transaction_id": "8z5jnKzjg3He9WAWr1NNIJwnpoNQRnixkqJAl",
            "type": "SPECIAL",
            "unofficial_currency_code": null
        }
    """.trimIndent()

    val transactionDetail: TransactionDetail = serializer.decodeFromString(detailStub)
}
