package com.babaetskv.muspert.utils

import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder

fun formatTime(timeMillis: Long): String = if (timeMillis <= 0) "00:00" else {
    Period(timeMillis).normalizedStandard().let {
        val formatter = PeriodFormatterBuilder()
            .appendHours()
            .appendSeparator(":")
            .printZeroIfSupported()
            .minimumPrintedDigits(2)
            .appendMinutes()
            .appendSeparator(":")
            .appendSeconds()
            .toFormatter()
        formatter.print(it)
    }
}
