package com.example.hotelbooking.features.transaction.di

import com.example.hotelbooking.features.transaction.data.repository.TransactionRepositoryImpl
import com.example.hotelbooking.features.transaction.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTransactionRepository(
        firestore: FirebaseFirestore
    ): TransactionRepository {
        return TransactionRepositoryImpl(firestore)
    }
}