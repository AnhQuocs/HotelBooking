package com.example.hotelbooking.features.chat.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.hotelbooking.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getInitials(name: String): String {
    return name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(3)
        .joinToString("") { it.first().uppercase() }
}


fun formatTimestamp24h(timestamp: Long): String {
    val zone = ZoneId.of("Asia/Ho_Chi_Minh")

    val tsMillis = if (timestamp < 1_000_000_000_000L) timestamp * 1000 else timestamp

    val dt = Instant.ofEpochMilli(tsMillis).atZone(zone)
    val now = ZonedDateTime.now(zone)

    val timePart = dt.format(DateTimeFormatter.ofPattern("HH:mm"))

    return when {
        dt.toLocalDate() == now.toLocalDate() -> {
            timePart
        }

        dt.year == now.year -> {
            timePart + dt.format(DateTimeFormatter.ofPattern(" dd/MM"))
        }

        else -> {
            timePart + dt.format(DateTimeFormatter.ofPattern(" dd/MM/yyyy"))
        }
    }
}

fun formatTimeOnly(timestamp: Long): String {
    val zone = ZoneId.of("Asia/Ho_Chi_Minh")
    val tsMillis = if (timestamp < 1_000_000_000_000L) timestamp * 1000 else timestamp
    return Instant.ofEpochMilli(tsMillis).atZone(zone).format(DateTimeFormatter.ofPattern("HH:mm"))
}

@Composable
fun formatDateHeader(timestamp: Long): String {
    val zone = ZoneId.of("Asia/Ho_Chi_Minh")
    val tsMillis = if (timestamp < 1_000_000_000_000L) timestamp * 1000 else timestamp
    val date = Instant.ofEpochMilli(tsMillis).atZone(zone).toLocalDate()
    val now = LocalDate.now(zone)

    return when {
        date == now -> stringResource(R.string.today)
        date == now.minusDays(1) -> stringResource(R.string.yesterday)
        date.year == now.year -> date.format(DateTimeFormatter.ofPattern("dd/MM"))
        else -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
}

fun isSameDay(ts1: Long, ts2: Long): Boolean {
    val zone = ZoneId.of("Asia/Ho_Chi_Minh")
    val d1 = Instant.ofEpochMilli(if (ts1 < 1_000_000_000_000L) ts1 * 1000 else ts1).atZone(zone).toLocalDate()
    val d2 = Instant.ofEpochMilli(if (ts2 < 1_000_000_000_000L) ts2 * 1000 else ts2).atZone(zone).toLocalDate()
    return d1 == d2
}