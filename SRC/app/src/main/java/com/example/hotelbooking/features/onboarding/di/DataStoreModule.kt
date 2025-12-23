package com.example.hotelbooking.features.onboarding.di

import android.content.Context
import com.example.hotelbooking.features.onboarding.data.local.OnboardingDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideOnboardingDataStore(
        @ApplicationContext context: Context
    ): OnboardingDataStore {
        return OnboardingDataStore(context)
    }
}