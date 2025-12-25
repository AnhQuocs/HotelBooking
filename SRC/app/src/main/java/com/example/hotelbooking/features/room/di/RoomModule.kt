package com.example.hotelbooking.features.room.di

import com.example.tomn_test.features.room.data.repository.RoomRepositoryImpl
import com.example.tomn_test.features.room.data.source.FirebaseRoomDataSource
import com.example.tomn_test.features.room.domain.repository.RoomRepository
import com.example.tomn_test.features.room.domain.usecase.GetRoomByIdUseCase
import com.example.tomn_test.features.room.domain.usecase.GetRoomsByHotelIdUseCase
import com.example.tomn_test.features.room.domain.usecase.RoomUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideFirebaseRoomDataSource(): FirebaseRoomDataSource {
        return FirebaseRoomDataSource()
    }

    @Provides
    @Singleton
    fun provideRoomRepository(
        dataSource: FirebaseRoomDataSource
    ): RoomRepository {
        return RoomRepositoryImpl(dataSource)
    }

    @Provides
    fun provideRoomUseCases(repository: RoomRepository) = RoomUseCases(
        getRoomsByHotelIdUseCase = GetRoomsByHotelIdUseCase(repository),
        getRoomByIdUseCase = GetRoomByIdUseCase(repository)
    )
}