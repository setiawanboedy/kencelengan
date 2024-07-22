package com.masjidjalancahaya.kencelenganreminder.utils.conversion

import java.time.LocalDateTime

interface ReminderTimeConversion {

    fun toZonedEpochMilli(
        startLocalDateTime: LocalDateTime,
        dateTimeConversion: DateTimeConversion
    ): Long?
}