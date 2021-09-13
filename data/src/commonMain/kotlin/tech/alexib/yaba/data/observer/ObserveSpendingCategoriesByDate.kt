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
package tech.alexib.yaba.data.observer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt
import tech.alexib.yaba.SubjectInteractor
import tech.alexib.yaba.data.repository.TransactionRepository
import tech.alexib.yaba.model.AllCategoriesSpend
import tech.alexib.yaba.model.RangeOption
import tech.alexib.yaba.model.toDatePair

class ObserveSpendingCategoriesByDate(
    private val transactionRepository: TransactionRepository
) : SubjectInteractor<ObserveSpendingCategoriesByDate.Params, AllCategoriesSpend>() {

    override fun createObservable(params: Params): Flow<AllCategoriesSpend> {
        val today = Clock.System.todayAt(TimeZone.currentSystemDefault())
        val rangeOption = params.rangeOption
            ?: RangeOption.values()[today.monthNumber - 1]
        val (start, end) = rangeOption.toDatePair()

        return transactionRepository.spendingCategoriesByDate(start, end).flatMapLatest {
            flow {
                emit(AllCategoriesSpend.from(rangeOption, it))
            }
        }
    }

    data class Params(val rangeOption: RangeOption? = null)
}
