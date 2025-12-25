package com.example.hotelbooking.features.review.di

import com.example.hotelbooking.features.review.data.repository.ReviewRepositoryImpl
import com.example.hotelbooking.features.review.data.source.FirebaseReviewDataSource
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository
import com.example.hotelbooking.features.review.domain.usecase.GetReviewsByServiceIdUseCase
import com.example.hotelbooking.features.review.domain.usecase.ReviewUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReviewModule {

    @Provides
    @Singleton
    fun provideFirebaseReviewDataSource(): FirebaseReviewDataSource {
        return FirebaseReviewDataSource()
    }

    @Provides
    @Singleton
    fun provideReviewRepository(
        dataSource: FirebaseReviewDataSource
    ): ReviewRepository {
        return ReviewRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideReviewUseCase(repository: ReviewRepository) = ReviewUseCase(
        getReviewsByServiceIdUseCase = GetReviewsByServiceIdUseCase(repository)
    )
}