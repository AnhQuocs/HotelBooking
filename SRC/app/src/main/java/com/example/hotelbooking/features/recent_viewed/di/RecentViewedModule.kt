package com.example.hotelbooking.features.recent_viewed.di

import com.example.hotelbooking.features.recent_viewed.data.repository.RecentViewedRepositoryImpl
import com.example.hotelbooking.features.recent_viewed.domain.repository.RecentViewedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RecentViewedModule {

    @Binds
    abstract fun bindRecentViewedRepository(
        impl: RecentViewedRepositoryImpl
    ): RecentViewedRepository
}