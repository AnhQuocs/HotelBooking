package com.example.hotelbooking.features.profile.util

import java.text.NumberFormat
import java.util.Locale

fun formatCurrencyUSD(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(amount)
}