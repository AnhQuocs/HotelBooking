package com.example.hotelbooking.features.hotel.presentation.util

import android.content.Context
import android.location.Geocoder
import com.example.hotelbooking.utils.LangUtils
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
    val appLocale = Locale(LangUtils.currentLang)

    val geocoder = Geocoder(context, appLocale)

    return try {
        println("DEBUG: Yêu cầu địa chỉ bằng ngôn ngữ: ${appLocale.language}")

        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0) ?: ""
        } else ""
    } catch (e: Exception) {
        ""
    }
}

fun formatDisplayAddress(rawAddress: String): String {
    val plusCodeRegex = Regex("^[A-Z0-9]{4,8}\\+[A-Z0-9]{2,3},\\s*")
    return rawAddress.replace(plusCodeRegex, "").trim()
}