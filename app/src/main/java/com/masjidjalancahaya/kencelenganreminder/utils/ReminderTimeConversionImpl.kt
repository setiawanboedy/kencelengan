package com.masjidjalancahaya.kencelenganreminder.utils

import java.time.LocalDateTime

class ReminderTimeConversionImpl : ReminderTimeConversion {

    override fun toZonedEpochMilli(
        startLocalDateTime: LocalDateTime,
        dateTimeConversion: DateTimeConversion
    ): Long {
        return dateTimeConversion.localDateTimeToZonedEpochMilli(
            startLocalDateTime.minusMinutes(1)
        )
    }
}