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
package tech.alexib.yaba.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayAt

enum class RangeOption(val value: String) {
    January("January"),
    February("February"),
    March("March"),
    April("April"),
    May("May"),
    June("June"),
    July("July"),
    August("August"),
    September("September"),
    October("October"),
    November("November"),
    December("December"),
    ThisYear("This year"),
    LastYear("Last year"),
    AllTime("All time");
}
@Suppress("MagicNumber")
fun RangeOption.toDatePair(): Pair<LocalDate, LocalDate> {
    val today = Clock.System.todayAt(TimeZone.currentSystemDefault())
    return when (val ordinal = this.ordinal) {
        today.monthNumber - 1 -> {
            val start = today.minus(today.dayOfMonth, DateTimeUnit.DAY)
            start to today
        }
        in 0..11 -> {
            val year = today.year
            val month = ordinal + 1
            val start = LocalDate(year, monthNumber = month, 1)
            val end = start.plus(DatePeriod(months = 1)).minus(1, DateTimeUnit.DAY)

            start to end
        }
        else -> when (this) {
            RangeOption.AllTime -> today.minus(DatePeriod(years = 5)) to today
            RangeOption.LastYear -> {
                val year = today.year - 1
                val start = LocalDate(year, 1, 1)
                val end = LocalDate(year, 12, 31)
                start to end
            }
            else -> {
                val year = today.year
                val start = LocalDate(year, 1, 1)
                val end = LocalDate(year, 12, 31)
                start to end
            }
        }
    }
}
