package com.example.hotelbooking.features.booking.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hotelbooking.features.booking.data.repository.BookingRepositoryImpl
import com.example.hotelbooking.features.booking.domain.usecase.update.ExpirePendingBookingsUseCase
import com.google.firebase.firestore.FirebaseFirestore

class BookingCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val repository = BookingRepositoryImpl(firestore)
            val expireUseCase = ExpirePendingBookingsUseCase(repository)
            expireUseCase()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}