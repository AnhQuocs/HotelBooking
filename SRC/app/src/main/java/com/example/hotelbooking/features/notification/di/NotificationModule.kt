package com.example.hotelbooking.features.notification.di

import android.content.Context
import com.example.hotelbooking.features.notification.data.local.dao.NotificationDao
import com.example.hotelbooking.features.notification.data.repository.NotificationRepositoryImpl
import com.example.hotelbooking.features.notification.domain.repository.NotificationRepository
import com.example.hotelbooking.features.notification.domain.usecase.GetNotificationsUseCase
import com.example.hotelbooking.features.notification.domain.usecase.MarkNotificationAsReadUseCase
import com.example.hotelbooking.features.notification.domain.usecase.NotificationUseCases
import com.example.hotelbooking.features.notification.domain.usecase.SaveNotificationUseCase
import com.example.hotelbooking.features.notification.util.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(dao: NotificationDao): NotificationRepository {
        return NotificationRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideNotificationUseCases(repository: NotificationRepository) = NotificationUseCases(
        saveNotificationUseCase = SaveNotificationUseCase(repository),
        getNotificationsUseCase = GetNotificationsUseCase(repository),
        markNotificationAsReadUseCase = MarkNotificationAsReadUseCase(repository)
    )
}