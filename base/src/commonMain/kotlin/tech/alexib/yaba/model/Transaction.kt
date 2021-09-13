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
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import tech.alexib.yaba.util.UuidSerializer

@Serializable
enum class TransactionType {
    DIGITAL,
    PLACE,
    SPECIAL,
    UNRESOLVED
}

@Serializable
data class Transaction(
    val id: Uuid,
    val accountId: Uuid,
    val name: String,
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val category: String? = null,
    val subcategory: String? = null,
    val isoCurrencyCode: String? = null,
    val merchantName: String? = null,
    val pending: Boolean = false
) {
    companion object {}
}

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
    val label = "$institutionName \n $accountName ****$accountMask"
}

data class CategorySpend(
    val total: Double,
    val rangeOption: RangeOption,
    val category: String,
    val percentage: Float
)

data class AllCategoriesSpend(
    val total: Double,
    val rangeOption: RangeOption,
    val spend: List<CategorySpend>
) {
    companion object {
        fun from(
            rangeOption: RangeOption,
            categories: List<Pair<String, Double>>
        ): AllCategoriesSpend {
            val spendingCategories = categories.filter { it.second >= 0 }
            val sum = spendingCategories.sumOf { it.second }

            return AllCategoriesSpend(
                sum,
                rangeOption,
                spendingCategories.sortedByDescending { it.second / sum }.map {
                    CategorySpend(
                        total = it.second,
                        category = it.first,
                        percentage = (it.second / sum).toFloat(),
                        rangeOption = rangeOption
                    )
                }
            )
        }
    }
}

@Serializable
enum class TransactionCategory(val value: String) {
    Bank_Fees("Bank Fees"),
    Recreation("Recreation"),
    Tax("Tax"),
    Shops("Shopping"),
    Healthcare("Healthcare"),
    Transfer("Transfer"),
    Food_and_Drink("Food and Drink"),
    Travel("Travel"),
    Service("Service"),
    Community("Community"),
    Interest("Interest");
}
