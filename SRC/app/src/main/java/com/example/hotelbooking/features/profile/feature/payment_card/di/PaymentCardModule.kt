package com.example.hotelbooking.features.profile.feature.payment_card.di

import com.example.hotelbooking.features.profile.feature.payment_card.data.repository.PaymentCardRepositoryImpl
import com.example.hotelbooking.features.profile.feature.payment_card.domain.repository.PaymentCardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentCardModule {

    @Binds
    abstract fun bindPaymentCardRepository(
        impl: PaymentCardRepositoryImpl
    ): PaymentCardRepository
}