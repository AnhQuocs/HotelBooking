package com.example.hotelbooking.features.booking.di

import com.example.hotelbooking.features.booking.data.repository.BookingRepositoryImpl
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.booking.domain.usecase.BookingUseCases
import com.example.hotelbooking.features.booking.domain.usecase.create.CreateBookingUseCase
import com.example.hotelbooking.features.booking.domain.usecase.delete.CancelBookingUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.CheckAvailabilityUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.CheckExpiredBookingsUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingByIdUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingsByUserUseCase
import com.example.hotelbooking.features.booking.domain.usecase.read.GetBookingsUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.ExpirePendingBookingsUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.UpdateBookingUseCase
import com.example.hotelbooking.features.booking.domain.usecase.update.UpdateStatusUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookingModule {

    @Provides
    @Singleton
    fun provideBookingRepository(
        firestore: FirebaseFirestore
    ): BookingRepository {
        return BookingRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideBookingUseCases(repository: BookingRepository): BookingUseCases {
        return BookingUseCases(
            checkAvailabilityUseCase = CheckAvailabilityUseCase(repository),
            createBookingUseCase = CreateBookingUseCase(repository),
            cancelBookingUseCase = CancelBookingUseCase(repository),
            updateBookingUseCase = UpdateBookingUseCase(repository),
            updateStatusUseCase = UpdateStatusUseCase(repository),
            getBookingsByUserUseCase = GetBookingsByUserUseCase(repository),
            getBookingByIdUseCase = GetBookingByIdUseCase(repository),
            getBookingsUseCase = GetBookingsUseCase(repository),
            expirePendingBookingsUseCase = ExpirePendingBookingsUseCase(repository),
            checkExpirePendingBookingsUseCase = CheckExpiredBookingsUseCase(repository)
        )
    }
}