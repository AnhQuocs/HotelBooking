package com.example.hotelbooking.features.booking.data.repository

import android.util.Log
import com.example.hotelbooking.features.booking.data.dto.BookingDto
import com.example.hotelbooking.features.booking.data.mapper.toDomain
import com.example.hotelbooking.features.booking.data.mapper.toDto
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

class BookingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BookingRepository {
    private val bookingsCollection = firestore.collection("bookings")

    private val zoneId = ZoneOffset.UTC

    private val cachedBookings = mutableMapOf<String, List<Booking>>()

    override suspend fun checkAvailability(
        hotelId: String,
        roomTypeId: String,
        totalRoom: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int {
        val startTs = Timestamp(startDate.atStartOfDay(ZoneOffset.UTC).toInstant())

        val snapshot = bookingsCollection.whereEqualTo("hotelId", hotelId)
            .whereEqualTo("roomTypeId", roomTypeId)
            .whereIn("status", listOf("CONFIRMED", "PENDING")).whereGreaterThan("endDate", startTs)
            .get().await()

        val bookings =
            snapshot.documents.mapNotNull { it.toObject(BookingDto::class.java)?.toDomain() }

        val requestedNights = startDate.datesUntil(endDate).toList()
        val bookedPerNight = requestedNights.associateWith { 0 }.toMutableMap()

        val now = Timestamp.now()

        bookings.forEach { booking ->
            cancelIfExpired(booking, now)
            countBookedNights(booking, requestedNights, bookedPerNight)
        }

        val maxBookedRooms = bookedPerNight.values.maxOrNull() ?: 0

        return (totalRoom - maxBookedRooms).coerceAtLeast(0)
    }

    private fun cancelIfExpired(booking: Booking, now: Timestamp) {
        if (
            booking.status == BookingStatus.PENDING &&
            booking.expireAt != null &&
            now.seconds > booking.expireAt.seconds
        ) {
            bookingsCollection.document(booking.bookingId)
                .update(
                    "status", BookingStatus.CANCELLED.name,
                    "cancelReason", CancelReason.TIMEOUT.name
                )
                .addOnFailureListener {
                    Log.e("LazyCheck", "Failed to cleanup booking ${booking.bookingId}")
                }
        }
    }

    private fun countBookedNights(
        booking: Booking,
        requestedNights: List<LocalDate>,
        bookedPerNight: MutableMap<LocalDate, Int>
    ) {
        val bStart = booking.startDate.toLocalDate()
        val bEnd = booking.endDate.toLocalDate()

        if (bStart.isBefore(requestedNights.last().plusDays(1)) &&
            bEnd.isAfter(requestedNights.first())
        ) {
            for (night in requestedNights) {
                if (!night.isBefore(bStart) && night.isBefore(bEnd)) {
                    bookedPerNight[night] = bookedPerNight[night]!! + 1
                }
            }
        }
    }

    override suspend fun createBooking(
        booking: Booking, availableRooms: Int, expireAt: Timestamp
    ): Booking {
        if (availableRooms < 1) {
            throw Exception("Room sold out just now!")
        }

        val docRef = bookingsCollection.document()

        val finalBooking = booking.copy(
            bookingId = docRef.id, status = BookingStatus.PENDING, expireAt = expireAt
        )

        docRef.set(finalBooking.toDto()).await()

        invalidateCache()

        return finalBooking
    }

    override suspend fun cancelBooking(bookingId: String, reason: CancelReason): Boolean {
        return try {
            bookingsCollection
                .document(bookingId)
                .update(
                    "status", BookingStatus.CANCELLED.name,
                    "cancelReason", reason.name
                )
                .await()

            invalidateCache()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateBooking(booking: Booking): Boolean {
        return try {
            bookingsCollection.document(booking.bookingId)
                .set(booking, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateStayStatus(
        bookingId: String, newStatus: StayStatus
    ): Booking {
        val docRef = bookingsCollection.document(bookingId)

        docRef.update("stayStatus", newStatus.name).await()

        val updatedSnapshot = docRef.get().await()
        val updatedBooking = updatedSnapshot.toObject(BookingDto::class.java)
            ?: throw Exception("Booking not found after update")

        invalidateCache()

        return updatedBooking.toDomain()
    }

    override suspend fun getBookingsByUser(userId: String): List<Booking> {
        cachedBookings[userId]?.let { return it }

        val snapshot = bookingsCollection.whereEqualTo("userId", userId)
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .await()

        val bookings =
            snapshot.documents.mapNotNull { it.toObject(BookingDto::class.java)?.toDomain() }

        cachedBookings[userId] = bookings
        return bookings
    }

    override suspend fun getBookingById(bookingId: String): Booking {
        val snapshot = bookingsCollection.document(bookingId).get().await()
        val bookingDto = snapshot.toObject(BookingDto::class.java)
            ?: throw Exception("Booking with ID $bookingId not found")
        return bookingDto.toDomain()
    }

    override suspend fun getBookings(
        hotelId: String,
        roomTypeId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        statuses: List<BookingStatus>
    ): List<Booking> {
        val startTs = Timestamp(startDate.atStartOfDay(zoneId).toInstant())
        val endTs = Timestamp(endDate.plusDays(1).atStartOfDay(zoneId).toInstant())

        val snapshot = bookingsCollection.whereEqualTo("hotelId", hotelId)
            .whereEqualTo("roomTypeId", roomTypeId).whereIn("status", statuses.map { it.name })
            .get().await()

        return snapshot.documents.mapNotNull { it.toObject(BookingDto::class.java)?.toDomain() }
            .filter {
                it.startDate.seconds < endTs.seconds && it.endDate.seconds > startTs.seconds
            }
    }

    override suspend fun expirePendingBookings() {
        val timeoutMillis = 10 * 60 * 1000L
        val cutoffTime = Timestamp(Date(System.currentTimeMillis() - timeoutMillis))

        try {
            val snapshot = bookingsCollection.whereEqualTo("status", "PENDING")
                .whereLessThan("createdAt", cutoffTime).get().await()

            if (snapshot.isEmpty) return

            val batch = firestore.batch()

            snapshot.documents.forEach { document ->
                batch.update(document.reference, "status", BookingStatus.CANCELLED.name)
            }

            batch.commit().await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun checkAndCancelExpiredBookings(userId: String): Result<Int> {
        return try {
            val now = Timestamp.now()

            val snapshot = bookingsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", BookingStatus.PENDING.name)
                .get()
                .await()

            if (snapshot.isEmpty) return Result.success(0)

            val batch = firestore.batch()
            var cancelCount = 0

            for (document in snapshot.documents) {
                val expireAt = document.getTimestamp("expireAt")

                if (expireAt != null && now.seconds > expireAt.seconds) {
                    batch.update(document.reference,
                        "status", BookingStatus.CANCELLED.name,
                        "cancelReason", CancelReason.TIMEOUT.name
                    )
                    cancelCount++
                }
            }

            if (cancelCount > 0) {
                batch.commit().await()
            }

            Result.success(cancelCount)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun confirmBookingPayment(bookingId: String, transactionId: String): Result<Unit> = try {
        val batch = firestore.batch()

        val bookingRef = bookingsCollection.document(bookingId)
        val transactionRef = firestore.collection("transactions").document(transactionId)

        // Update Booking
        batch.update(bookingRef, "status", "CONFIRMED")

        // Update Transaction
        batch.update(transactionRef, mapOf(
            "status" to TransactionStatus.PAID.name,
            "updatedAt" to System.currentTimeMillis()
        ))

        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun cancelBookingAndTransaction(
        bookingId: String,
        cancelReason: String
    ): Result<Unit> {
        return try {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            val bookingRef = firestore.collection("bookings").document(bookingId)

            val transactionQuery = firestore.collection("transactions")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("bookingId", bookingId)
                .whereIn("status", listOf("PENDING", "PAID"))
                .limit(1)
                .get()
                .await()

            val transactionDoc = transactionQuery.documents.firstOrNull()
            val transactionRef = transactionDoc?.reference

            if (transactionRef == null) {
                Log.e("CANCEL_DEBUG", "No valid transaction found to cancel.")
                return Result.failure(Exception("No valid transaction found."))
            }

            firestore.runTransaction { firestoreTransaction ->

                val bookingSnapshot = firestoreTransaction.get(bookingRef)
                if (!bookingSnapshot.exists()) {
                    throw Exception("Booking does not exist.")
                }

                val currentBookingStatus = bookingSnapshot.getString("status") ?: "PENDING"

                val newTransactionStatus = if (currentBookingStatus == "CONFIRMED") "REFUND" else "CANCELLED"

                firestoreTransaction.update(
                    bookingRef,
                    "status", "CANCELLED",
                    "cancelReason", cancelReason,
                    "updatedAt", Timestamp.now()
                )

                firestoreTransaction.update(transactionRef, "status", newTransactionStatus)
                firestoreTransaction.update(transactionRef, "updatedAt", System.currentTimeMillis())

                if (newTransactionStatus == "REFUND") {
                    firestoreTransaction.update(transactionRef, "refundedAt", System.currentTimeMillis())
                }

                null
            }.await()

            invalidateCache()

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("CANCEL_DEBUG", "!!! ERROR: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun rebookTransaction(
        bookingId: String,
        updatedBooking: Booking,
        newTransaction: Transaction
    ): Result<Unit> {
        return try {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            Log.d("REBOOK_DEBUG", "=== REBOOK START | bookingId=$bookingId | user=$currentUserId ===")

            val bookingRef = bookingsCollection.document(bookingId)
            val transactionRef = firestore.collection("transactions").document()

            Log.d("REBOOK_DEBUG", "TxRef created: ${transactionRef.id}")

            firestore.runTransaction { firestoreTransaction ->

                val finalBooking = updatedBooking.copy(
                    status = BookingStatus.CONFIRMED,
                    cancelReason = null,
                    updatedAt = Timestamp.now()
                )
                firestoreTransaction.set(bookingRef, finalBooking)

                Log.d("REBOOK_DEBUG", "Booking updated -> CONFIRMED")

                val pendingTx = newTransaction.copy(
                    id = transactionRef.id,
                    bookingId = bookingId,
                    userId = currentUserId,
                    status = TransactionStatus.PENDING,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                firestoreTransaction.set(transactionRef, pendingTx)

                Log.d("REBOOK_DEBUG", "Transaction created -> PENDING")

                firestoreTransaction.update(
                    transactionRef,
                    mapOf(
                        "status" to "PAID",
                        "updatedAt" to System.currentTimeMillis()
                    )
                )

                Log.d("REBOOK_DEBUG", "Transaction updated -> PAID")

                null
            }.await()

            Log.d("REBOOK_DEBUG", "=== REBOOK SUCCESS | bookingId=$bookingId ===")
            invalidateCache()
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(
                "REBOOK_DEBUG",
                "!!! REBOOK FAILED | bookingId=$bookingId | reason=${e.message}"
            )
            Result.failure(e)
        }
    }

    // --- Helper Extensions ---
    private fun Timestamp.toLocalDate(): LocalDate {
        return this.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
    }

    private fun invalidateCache() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { cachedBookings.remove(it) }
    }

    override fun clearCache(userId: String) {
        cachedBookings.remove(userId)
    }
}