package com.example.hotelbooking.features.hotel.domain.di

import com.example.hotelbooking.features.hotel.data.repository.HotelRepositoryImpl
import com.example.hotelbooking.features.hotel.data.source.FirebaseHotelDataSource
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.features.hotel.domain.usecase.AdminHotelUseCases
import com.example.hotelbooking.features.hotel.domain.usecase.HotelUseCases
import com.example.hotelbooking.features.hotel.domain.usecase.create.AddHotelUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.GetAllHotelsUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.GetHotelByIdUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.GetHotelsByAdminIdUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.GetRecommendedHotelsUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.update.UpdateHotelRatingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HotelModule {

    @Provides
    @Singleton
    fun provideFirebaseHotelDataSource(): FirebaseHotelDataSource {
        return FirebaseHotelDataSource()
    }

    @Provides
    @Singleton
    fun provideHotelRepository(
        dataSource: FirebaseHotelDataSource
    ): HotelRepository {
        return HotelRepositoryImpl(dataSource)
    }

    @Provides
    fun provideHotelUseCase(repository: HotelRepository) = HotelUseCases(
        getAllHotelsUseCase = GetAllHotelsUseCase(repository),
        getHotelByIdUseCase = GetHotelByIdUseCase(repository),
        getRecommendedHotelsUseCase = GetRecommendedHotelsUseCase(repository),
    )

    @Provides
    fun provideAdminHotelUseCase(repository: HotelRepository) = AdminHotelUseCases(
        addHotelUseCase = AddHotelUseCase(repository),
        getHotelsByAdminIdUseCase = GetHotelsByAdminIdUseCase(repository)
    )

    @Provides
    fun provideUpdateHotelRatingUseCase(repository: HotelRepository) =
        UpdateHotelRatingUseCase(repository)

    @Provides
    fun provideAddHotelCase(repository: HotelRepository) =
        AddHotelUseCase(repository)
}