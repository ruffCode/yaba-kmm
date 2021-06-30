package tech.alexib.yaba.kmm.android.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val localDateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

fun LocalDate.longFormat():String = localDateFormatter.format(this.toJavaLocalDate())