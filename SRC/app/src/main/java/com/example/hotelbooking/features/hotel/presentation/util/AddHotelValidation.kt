package com.example.hotelbooking.features.hotel.presentation.util

object AddHotelValidation {
    fun validateBasicInfo(name: String, des: String): Boolean {
        val wordsName = name
            .trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        val wordsDes = des
            .trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        return wordsName.isNotEmpty() &&
                wordsName.size <= 5 &&
                wordsDes.isNotEmpty() &&
                wordsDes.size <= 20
    }
}