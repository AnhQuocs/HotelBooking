package com.example.hotelbooking.features.profile.util

import android.content.Context
import com.example.hotelbooking.R
import com.example.hotelbooking.features.profile.ui.user.PrivacySection

fun getPrivacyPolicySections(context: Context) = listOf(
    PrivacySection(
        context.getString(R.string.privacy_title_1),
        context.getString(R.string.privacy_content_1)
    ),
    PrivacySection(
        context.getString(R.string.privacy_title_2),
        context.getString(R.string.privacy_content_2)
    ),
    PrivacySection(
        context.getString(R.string.privacy_title_3),
        context.getString(R.string.privacy_content_3)
    ),
    PrivacySection(
        context.getString(R.string.privacy_title_4),
        context.getString(R.string.privacy_content_4)
    ),
    PrivacySection(
        context.getString(R.string.privacy_title_5),
        context.getString(R.string.privacy_content_5)
    ),
    PrivacySection(
        context.getString(R.string.privacy_title_6),
        context.getString(R.string.privacy_content_6)
    )
)