package com.example.hotelbooking.features.profile.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri
import com.example.hotelbooking.R

object SupportUtils {
    fun getDeviceInfo(context: Context): String {
        val appVersion = try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "Unknown"
        }

        return """
            
            -----------------------------------------
            THÔNG TIN THIẾT BỊ (DEVICE INFO)
            -----------------------------------------
            - App Version: $appVersion
            - Device Model: ${Build.MODEL} (${Build.PRODUCT})
            - Manufacturer: ${Build.MANUFACTURER}
            - Android Version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
            - Device Brand: ${Build.BRAND}
            -----------------------------------------
            (Thông tin này giúp chúng tôi xử lý sự cố nhanh hơn)
        """.trimIndent()
    }
}

fun sendSupportEmail(
    context: Context,
    subject: String,
    content: String,
    includeDeviceInfo: Boolean
) {
    val supportEmail = "anhquocb435@gmail.com"

    val finalContent = if (includeDeviceInfo) {
        "$content\n\n${SupportUtils.getDeviceInfo(context)}"
    } else {
        content
    }

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
        putExtra(Intent.EXTRA_SUBJECT, "[HotelBooking Support] $subject")
        putExtra(Intent.EXTRA_TEXT, finalContent)
    }

    try {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.choose_email_app)
            )
        )
    } catch (e: Exception) {
        Toast.makeText(
            context,
            context.getString(R.string.no_email_app_found),
            Toast.LENGTH_SHORT
        ).show()
    }
}