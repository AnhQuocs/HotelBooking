package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import java.util.Calendar


fun combineDateAndTime(dateTimestamp: com.google.firebase.Timestamp, timeString: String?): Long {
    val date = dateTimestamp.toDate()
    val calendar = Calendar.getInstance().apply {
        time = date
    }

    val timeParts = timeString?.split(":") ?: listOf("0", "0")
    val hours = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
    val minutes = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    calendar.set(Calendar.HOUR_OF_DAY, hours)
    calendar.set(Calendar.MINUTE, minutes)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis
}