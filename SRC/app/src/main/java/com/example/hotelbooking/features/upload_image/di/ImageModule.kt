package com.example.hotelbooking.features.upload_image.di

import com.example.hotelbooking.features.upload_image.data.repository.ImageRepositoryImpl
import com.example.hotelbooking.features.upload_image.domain.repository.ImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageModule {
    @Binds
    abstract fun bindImageRepository(
        impl: ImageRepositoryImpl
    ): ImageRepository
}