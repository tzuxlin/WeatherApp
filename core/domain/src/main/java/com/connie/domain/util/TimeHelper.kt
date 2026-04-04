package com.connie.domain.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeHelper {

    val hourMinuteFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    val dayOfWeekFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEEE")

    fun now(): ZonedDateTime {
        return ZonedDateTime.now()
    }

    fun getZonedDateTime(
        timestampSeconds: Long,
        timezone: Int,
    ): ZonedDateTime {
        return Instant.ofEpochSecond(timestampSeconds)
            .atZone(getZoneOffset(timezone))
    }

    fun getStartOfDay(
        timestampSeconds: Long,
        timezone: Int,
    ): ZonedDateTime {
        return getZonedDateTime(timestampSeconds, timezone)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
    }

    fun format(
        zonedDateTime: ZonedDateTime,
        formatter: DateTimeFormatter,
    ): String {
        return zonedDateTime.format(formatter)
    }

    fun format(
        timestampSeconds: Long,
        timezone: Int,
        formatter: DateTimeFormatter,
    ): String {
        return getZonedDateTime(timestampSeconds, timezone).format(formatter)
    }

    private fun getZoneOffset(timezone: Int): ZoneOffset {
        return ZoneOffset.ofTotalSeconds(timezone)
    }
}