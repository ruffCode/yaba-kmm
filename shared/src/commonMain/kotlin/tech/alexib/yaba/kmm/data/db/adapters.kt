package tech.alexib.yaba.kmm.data.db

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

class UUIDAdapter : ColumnAdapter<Uuid, String> {
    override fun decode(databaseValue: String): Uuid =
        uuidFrom(databaseValue)

    override fun encode(value: Uuid): String =
        value.toString()
}