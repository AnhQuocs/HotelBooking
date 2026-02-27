package com.example.hotelbooking.features.location.utils

import java.text.Normalizer
import java.util.regex.Pattern

object LocationTranslator {
    private fun removeAccents(str: String): String {
        val normalized = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized).replaceAll("").replace('đ', 'd').replace('Đ', 'D')
    }

    fun toEnglish(viName: String): String {
        if (viName.isBlank()) return ""
        if (viName.contains("Hồ Chí Minh")) return "Ho Chi Minh City"
        if (viName.contains("Hà Nội")) return "Hanoi"

        val noAccent = removeAccents(viName)
        return when {
            noAccent.startsWith("Thanh pho ") -> noAccent.removePrefix("Thanh pho ") + " City"
            noAccent.startsWith("Tinh ") -> noAccent.removePrefix("Tinh ") + " Province"
            noAccent.startsWith("Quan ") -> noAccent.removePrefix("Quan ") + " District"
            noAccent.startsWith("Huyen ") -> noAccent.removePrefix("Huyen ") + " District"
            noAccent.startsWith("Thi xa ") -> noAccent.removePrefix("Thi xa ") + " Town"
            noAccent.startsWith("Phuong ") -> noAccent.removePrefix("Phuong ") + " Ward"
            noAccent.startsWith("Xa ") -> noAccent.removePrefix("Xa ") + " Commune"
            noAccent.startsWith("Thi tran ") -> noAccent.removePrefix("Thi tran ") + " Township"
            else -> noAccent
        }.trim()
    }


    fun toEnglishStreet(viStreet: String): String {
        if (viStreet.isBlank()) return ""

        val noAccent = removeAccents(viStreet)

        val lowerStr = noAccent.lowercase()
        return when {
            lowerStr.startsWith("duong ") -> noAccent.substring(6) + " Street"
            lowerStr.startsWith("pho ") -> noAccent.substring(4) + " Street"
            lowerStr.startsWith("ngo ") -> "Alley " + noAccent.substring(4)
            lowerStr.startsWith("ngach ") -> "Sub-alley " + noAccent.substring(6)
            else -> noAccent
        }.trim()
    }
}