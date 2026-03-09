package com.example.hotelbooking.features.review.presentation.util

import android.content.Context
import com.example.hotelbooking.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeUtils {
    fun getRelativeTime(context: Context, timestampStr: String?): String {
        if (timestampStr.isNullOrEmpty()) return ""

        return try {
            val instant = Instant.parse(timestampStr)
            val past = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            val now = LocalDateTime.now()

            val seconds = ChronoUnit.SECONDS.between(past, now)
            val minutes = ChronoUnit.MINUTES.between(past, now)
            val hours = ChronoUnit.HOURS.between(past, now)
            val days = ChronoUnit.DAYS.between(past, now)

            when {
                seconds < 60 -> context.getString(R.string.time_just_now)
                minutes < 60 -> context.getString(R.string.time_minutes_ago, minutes)
                hours < 24 -> context.getString(R.string.time_hours_ago, hours)
                days < 7 -> context.getString(R.string.time_days_ago, days)
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    past.format(formatter)
                }
            }
        } catch (e: Exception) {
            timestampStr
        }
    }
}