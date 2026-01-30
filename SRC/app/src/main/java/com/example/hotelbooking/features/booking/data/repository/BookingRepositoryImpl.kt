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
import com.example.hotelbooking.features.transaction.data.mapper.toDto
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
        booking: Booking,
        roomTypeId: String,
        roomNumber: String,
        expireAt: Timestamp
    ): Booking {
        val bookingRef = bookingsCollection.document()
        val roomTypeRef = firestore.collection("rooms").document(roomTypeId)

        return firestore.runTransaction { transaction ->
            val roomTypeSnapshot = transaction.get(roomTypeRef)
            if (!roomTypeSnapshot.exists()) {
                throw Exception("Room type does not exist!")
            }

            val roomList = roomTypeSnapshot.get("roomList")
                ?.let { it as? List<*> }
                ?.mapNotNull { it as? Map<*, *> }
                ?: throw Exception("Room list data error!")

            val updatedRoomList = roomList.map { room ->
                if (room["roomNumber"] == roomNumber) {
                    val isAvailable = room["isAvailable"] as? Boolean ?: false
                    if (!isAvailable) {
                        throw Exception("Room $roomNumber has already been booked!")
                    }
                    room.toMutableMap().apply { this["isAvailable"] = false }
                } else {
                    room
                }
            }

            val finalBooking = booking.copy(
                bookingId = bookingRef.id,
                status = BookingStatus.PENDING,
                expireAt = expireAt
            )

            transaction.update(roomTypeRef, "roomList", updatedRoomList)
            transaction.set(bookingRef, finalBooking.toDto())

            finalBooking
        }.await().also {
            invalidateCache()
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

            val expiredDocs = snapshot.documents.filter { doc ->
                val expireAt = doc.getTimestamp("expireAt")
                expireAt != null && now.seconds > expireAt.seconds
            }

            if (expiredDocs.isEmpty()) return Result.success(0)

            firestore.runTransaction { transaction ->
                val roomTypeChanges = mutableMapOf<String, MutableList<String>>()

                for (doc in expiredDocs) {
                    val roomTypeId = doc.getString("roomTypeId") ?: ""
                    val roomNumber = doc.getString("roomNumber") ?: ""

                    transaction.update(doc.reference,
                        "status", BookingStatus.CANCELLED.name,
                        "cancelReason", CancelReason.TIMEOUT.name,
                        "updatedAt", Timestamp.now()
                    )

                    if (roomTypeId.isNotEmpty() && roomNumber.isNotEmpty()) {
                        roomTypeChanges.getOrPut(roomTypeId) { mutableListOf() }.add(roomNumber)
                    }
                }

                roomTypeChanges.forEach { (typeId, numbersToFree) ->
                    val roomTypeRef = firestore.collection("rooms").document(typeId)
                    val roomTypeSnapshot = transaction.get(roomTypeRef)

                    if (roomTypeSnapshot.exists()) {
                        val currentRoomList = roomTypeSnapshot.get("roomList")
                            ?.let { it as? List<*> }
                            ?.mapNotNull { it as? Map<*, *> }
                            ?: throw Exception("Room list data error!")

                        val updatedRoomList = currentRoomList.map { room ->
                            val rNumber = room["roomNumber"] as? String
                            if (numbersToFree.contains(rNumber)) {
                                room.toMutableMap().apply { this["isAvailable"] = true }
                            } else {
                                room
                            }
                        }

                        transaction.update(roomTypeRef, "roomList", updatedRoomList)
                    }
                }
                null
            }.await()

            invalidateCache()
            Result.success(expiredDocs.size)

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
        cancelReason: String,
        cancelNote: String?
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

                val roomTypeId = bookingSnapshot.getString("roomTypeId") ?: ""
                val roomNumber = bookingSnapshot.getString("roomNumber") ?: ""
                val currentBookingStatus = bookingSnapshot.getString("status") ?: "PENDING"

                if (roomTypeId.isNotEmpty() && roomNumber.isNotEmpty()) {
                    val roomTypeRef = firestore.collection("rooms").document(roomTypeId)
                    val roomTypeSnapshot = firestoreTransaction.get(roomTypeRef)

                    if (roomTypeSnapshot.exists()) {
                        val currentRoomList = roomTypeSnapshot.get("roomList")
                            ?.let { it as? List<*> }
                            ?.mapNotNull { it as? Map<*, *> }
                            ?: throw Exception("Room list data error!")

                        val updatedRoomList = currentRoomList.map { room ->
                            if (room["roomNumber"] == roomNumber) {
                                room.toMutableMap().apply { this["isAvailable"] = true }
                            } else {
                                room
                            }
                        }
                        firestoreTransaction.update(roomTypeRef, "roomList", updatedRoomList)
                    }
                }

                firestoreTransaction.update(
                    bookingRef,
                    "status", "CANCELLED",
                    "cancelReason", cancelReason,
                    "cancelNote", cancelNote,
                    "updatedAt", Timestamp.now()
                )

                val newTransactionStatus = if (currentBookingStatus == "CONFIRMED") "REFUND" else "CANCELLED"

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
            val bookingRef = bookingsCollection.document(bookingId)
            val transactionRef = firestore.collection("transactions").document()

            firestore.runTransaction { tx ->

                val oldBookingSnapshot = tx.get(bookingRef)
                if (!oldBookingSnapshot.exists()) throw Exception("Booking not found")

                val oldRoomNumber = oldBookingSnapshot.getString("roomNumber") ?: ""
                val oldRoomTypeId = oldBookingSnapshot.getString("roomTypeId") ?: ""
                val newRoomNumber = updatedBooking.roomNumber
                val newRoomTypeId = updatedBooking.roomTypeId

                val oldRoomTypeRef = firestore.collection("rooms").document(oldRoomTypeId)
                val oldRoomTypeSnap = tx.get(oldRoomTypeRef)

                val newRoomTypeSnap = if (oldRoomTypeId == newRoomTypeId) {
                    oldRoomTypeSnap
                } else {
                    tx.get(firestore.collection("rooms").document(newRoomTypeId))
                }

                val oldRoomList = oldRoomTypeSnap.get("roomList")
                    ?.let { it as? List<*> }
                    ?.mapNotNull { it as? Map<*, *> }
                    ?: throw Exception("Invalid old room list")

                val updatedOldRoomList = oldRoomList.map { room ->
                    if (
                        room["roomNumber"] == oldRoomNumber &&
                        (oldRoomNumber != newRoomNumber || oldRoomTypeId != newRoomTypeId)
                    ) {
                        room.toMutableMap().apply { this["isAvailable"] = true }
                    } else room
                }

                val sourceListForNew = if (oldRoomTypeId == newRoomTypeId) {
                    updatedOldRoomList
                } else {
                    newRoomTypeSnap.get("roomList") ?.let { it as? List<*> }
                        ?.mapNotNull { it as? Map<*, *> }
                        ?: throw Exception("Invalid new room list")
                }

                val updatedNewRoomList = sourceListForNew.map { room ->
                    if (room["roomNumber"] == newRoomNumber) {
                        if (room["isAvailable"] == false && oldRoomNumber != newRoomNumber) {
                            throw Exception("Room $newRoomNumber is already occupied")
                        }
                        room.toMutableMap().apply { this["isAvailable"] = false }
                    } else room
                }

                if (oldRoomTypeId == newRoomTypeId) {
                    tx.update(oldRoomTypeRef, "roomList", updatedNewRoomList)
                } else {
                    tx.update(oldRoomTypeRef, "roomList", updatedOldRoomList)
                    val newRoomTypeRef = firestore.collection("rooms").document(newRoomTypeId)
                    tx.update(newRoomTypeRef, "roomList", updatedNewRoomList)
                }

                val finalBooking = updatedBooking.copy(
                    bookingId = bookingId,
                    status = BookingStatus.CONFIRMED,
                    updatedAt = Timestamp.now()
                )
                tx.set(bookingRef, finalBooking.toDto())

                val paidTx = newTransaction.copy(
                    id = transactionRef.id,
                    bookingId = bookingId,
                    userId = currentUserId,
                    status = TransactionStatus.PAID,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                tx.set(transactionRef, paidTx.toDto())

                null
            }.await()

            invalidateCache()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("REBOOK_TX", "Transaction failed: ${e.message}")
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