package com.masjidjalancahaya.kencelenganreminder.utils.conversion

import java.time.LocalDateTime

class ReminderTimeConversionImpl : ReminderTimeConversion {

    override fun toZonedEpochMilli(
        startLocalDateTime: LocalDateTime,
        dateTimeConversion: DateTimeConversion
    ): Long {
        return dateTimeConversion.localDateTimeToZonedEpochMilli(
            startLocalDateTime
        )
    }
}