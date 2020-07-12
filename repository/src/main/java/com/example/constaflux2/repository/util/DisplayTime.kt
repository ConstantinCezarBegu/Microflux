package com.example.constaflux2.repository.util

import com.example.constaflux2.data.EntryPublishedAtUnix
import com.example.constaflux2.data.EntryPublishedAtDisplay
import com.example.constaflux2.data.EntryPublishedAtRaw
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.Period

data class EntryTime(
    val entryPublishedAtUnix: EntryPublishedAtUnix,
    val entryPublishedAtDisplay: EntryPublishedAtDisplay,
    val entryPublishedAtRaw: EntryPublishedAtRaw
)

fun String.stringToEntryTime(): EntryTime {
    val offsetDateTime = OffsetDateTime.parse(this)
    val timeNow = LocalDateTime.now(offsetDateTime.toZonedDateTime().zone)
    val timeOther = offsetDateTime.toLocalDateTime()

    val duration = Duration.between(timeOther, timeNow)
    val deltaMinutes = duration.toMinutes()
    val deltaHours = duration.toHours()
    val deltaDays = duration.toDays()
    val periodDiff = Period.between(timeOther.toLocalDate(), timeNow.toLocalDate())
    val deltaYears = periodDiff.years
    val deltaMonth = periodDiff.months

    val entryPublishedAtDisplay =
        if (deltaMinutes < 60) if (deltaMinutes == 0L) "now" else "$deltaMinutes minute${if (deltaMinutes > 1) "s" else ""} ago"
        else if (deltaHours < 24) "$deltaHours hour${if (deltaHours > 1) "s" else ""} ago"
        else if (deltaDays < timeNow.toLocalDate().lengthOfMonth())
            if (deltaDays == 1L) "yesterday" else "$deltaDays days ago"
        else if (deltaMonth < 12) "$deltaMonth month${if (deltaMonth > 1) "s" else ""} ago"
        else "$deltaYears year${if (deltaYears > 1) "s" else ""} ago"


    return EntryTime(
        entryPublishedAtRaw = EntryPublishedAtRaw(this),
        entryPublishedAtDisplay = EntryPublishedAtDisplay(entryPublishedAtDisplay),
        entryPublishedAtUnix = EntryPublishedAtUnix(offsetDateTime.toEpochSecond())
    )
}