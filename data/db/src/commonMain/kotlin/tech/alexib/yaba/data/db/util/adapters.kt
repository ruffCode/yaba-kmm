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
package tech.alexib.yaba.data.db.util

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

class DateAdapter : ColumnAdapter<LocalDate, Long> {
    override fun decode(databaseValue: Long): LocalDate =
        Instant.fromEpochSeconds(databaseValue)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date

    override fun encode(value: LocalDate): Long =
        value.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds
}

class UuidAdapter : ColumnAdapter<Uuid, String> {
    override fun decode(databaseValue: String): Uuid =
        uuidFrom(databaseValue)

    override fun encode(value: Uuid): String =
        value.toString()
}
