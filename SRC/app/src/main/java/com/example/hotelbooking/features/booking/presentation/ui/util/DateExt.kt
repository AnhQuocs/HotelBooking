package com.example.hotelbooking.features.booking.presentation.ui.util

import kotlinx.datetime.LocalDate as KxLocalDate
import java.time.LocalDate as JavaLocalDate

fun KxLocalDate.toJavaLocalDate(): JavaLocalDate =
    JavaLocalDate.of(year, monthNumber, dayOfMonth)