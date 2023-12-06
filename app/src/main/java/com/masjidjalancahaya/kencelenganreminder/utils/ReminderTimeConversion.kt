package com.masjidjalancahaya.kencelenganreminder.utils

import java.time.LocalDateTime

interface ReminderTimeConversion {

    fun toZonedEpochMilli(
        startLocalDateTime: LocalDateTime,
        dateTimeConversion: DateTimeConversion
    ): Long?
}