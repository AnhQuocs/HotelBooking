package com.example.hotelbooking.features.language.domain.model

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    VIETNAMESE("vi");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return AppLanguage.entries.firstOrNull { it.code == code } ?: ENGLISH
        }
    }
}