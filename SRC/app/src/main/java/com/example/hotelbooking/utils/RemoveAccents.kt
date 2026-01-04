package com.example.hotelbooking.utils

import java.text.Normalizer
import java.util.regex.Pattern

fun String.removeAccents(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(temp).replaceAll("")
        .replace('đ', 'd').replace('Đ', 'D')
}