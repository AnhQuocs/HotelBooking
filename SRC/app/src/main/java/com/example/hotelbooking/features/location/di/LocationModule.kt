package com.example.hotelbooking.features.location.di

import android.content.Context
import com.example.hotelbooking.features.location.data.repository.LocationRepositoryImpl
import com.example.hotelbooking.features.location.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context
    ): LocationRepository {
        return LocationRepositoryImpl(context)
    }
}