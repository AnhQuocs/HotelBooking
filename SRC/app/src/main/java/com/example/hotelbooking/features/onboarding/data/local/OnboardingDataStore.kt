package com.example.hotelbooking.features.onboarding.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.hotelbooking.features.language.data.preference.languageDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object OnboardingPrefKeys {
    val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
}

class OnboardingDataStore(private val context: Context) {

    private val dataStore = context.languageDataStore

    suspend fun setOnboardingDone(done: Boolean) {
        dataStore.edit { prefs ->
            prefs[OnboardingPrefKeys.ONBOARDING_DONE] = done
        }
    }

    suspend fun isOnboardingDone(): Boolean {
        return dataStore.data
            .map { it[OnboardingPrefKeys.ONBOARDING_DONE] ?: false }
            .first()
    }
}