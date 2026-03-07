package com.example.hotelbooking.features.vouchers.presentation.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatLongToDate(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun formatLongToDateTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}