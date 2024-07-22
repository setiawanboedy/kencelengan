package com.masjidjalancahaya.kencelenganreminder.utils.conversion

import java.time.LocalDateTime

interface DateTimeConversion {
    fun localDateTimeToZonedEpochMilli(localDateTime: LocalDateTime): Long
    fun zonedEpochMilliToLocalDateTime(zonedEpochMilli: Long): LocalDateTime
}