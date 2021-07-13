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
package tech.alexib.yaba.android.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaLocalDate
import tech.alexib.yaba.model.Transaction
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val localDateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
val shotDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd")

fun LocalDate.longFormat(): String = localDateFormatter.format(this.toJavaLocalDate())
fun LocalDate.shortFormat(): String = shotDateFormatter.format(this.toJavaLocalDate())

val moneyFormat = DecimalFormat("#,###.00")

fun LocalDate.format(): String {
    val now = Clock.System.now()
    val date = this.atStartOfDayIn(TimeZone.currentSystemDefault())
    val diff = now.minus(date)
    return when (diff.inWholeDays) {
        in 0..30 -> this.shortFormat()
        else -> this.longFormat()
    }
}

fun Transaction.formattedDate() = this.date.format()
