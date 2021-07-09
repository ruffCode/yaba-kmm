package tech.alexib.yaba.kmm.android.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val localDateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
val shotDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd")

fun LocalDate.longFormat(): String = localDateFormatter.format(this.toJavaLocalDate())
fun LocalDate.shortFormat(): String = shotDateFormatter.format(this.toJavaLocalDate())

val moneyFormat = DecimalFormat("#,###.00")
