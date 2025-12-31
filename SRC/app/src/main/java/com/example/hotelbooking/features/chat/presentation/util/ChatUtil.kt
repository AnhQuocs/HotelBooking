package com.example.hotelbooking.features.chat.presentation.util

import java.time.Instant
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