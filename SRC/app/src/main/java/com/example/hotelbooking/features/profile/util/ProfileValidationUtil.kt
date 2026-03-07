package com.example.hotelbooking.features.profile.util

import android.content.Context
import com.example.hotelbooking.R

object ProfileValidationUtil {

    fun validateUsername(context: Context, username: String): String? {
        return when {
            username.isBlank() ->
                context.getString(R.string.username_empty_error)

            username.length < 8 ->
                context.getString(R.string.username_too_short)

            else -> null
        }
    }

    fun validateName(context: Context, name: String, label: String): String? {
        val nameRegex = Regex("^[\\p{L} .'-]+$")

        return when {
            name.isBlank() ->
                context.getString(R.string.name_empty_error, label)

            name.length < 2 ->
                context.getString(R.string.name_too_short, label)

            !name.matches(nameRegex) ->
                context.getString(R.string.name_invalid, label)

            else -> null
        }
    }

    fun validatePhone(context: Context, phone: String): String? {
        val phoneRegex = Regex("^0[0-9]{9}$")

        return when {
            phone.isBlank() ->
                context.getString(R.string.phone_empty_error)

            !phone.matches(phoneRegex) ->
                context.getString(R.string.phone_invalid)

            else -> null
        }
    }
}