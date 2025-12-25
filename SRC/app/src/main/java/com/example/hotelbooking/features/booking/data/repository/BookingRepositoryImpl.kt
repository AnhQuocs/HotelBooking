package com.example.hotelbooking.features.booking.data.repository

import com.example.hotelbooking.features.booking.data.dto.BookingDto
import com.example.hotelbooking.features.booking.data.mapper.toDomain
import com.example.hotelbooking.features.booking.data.mapper.toDto
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

class BookingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BookingRepository {
    private val bookingsCollection = firestore.collection("bookings")

    private val zoneId = ZoneOffset.UTC

    override suspend fun checkAvailability(
        hotelId: String,
        roomTypeId: String,
        totalRoom: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int {
        val startTs = Timestamp(startDate.atStartOfDay(ZoneOffset.UTC).toInstant())

        val snapshot = bookingsCollection
            .whereEqualTo("hotelId", hotelId)
            .whereEqualTo("roomTypeId", roomTypeId)
            .whereIn("status", listOf("CONFIRMED", "PENDING"))
            .whereGreaterThan("endDate", startTs)
            .get()
            .await()

        val bookings = snapshot.documents.mapNotNull { it.toObject(BookingDto::class.java)?.toDomain() }

        val requestedNights = startDate.datesUntil(endDate).toList()
        val bookedPerNight = requestedNights.associateWith { 0 }.toMutableMap()

        bookings.forEach { booking ->
            val bStart = booking.startDate.toLocalDate()
            val bEnd = booking.endDate.toLocalDate()

            if (bStart.isBefore(endDate) && bEnd.isAfter(startDate)) {

                for (night in requestedNights) {
                    if (!night.isBefore(bStart) && night.isBefore(bEnd)) {
                        bookedPerNight[night] = bookedPerNight[night]!! + 1
                    }
                }
            }
        }

        val maxBookedRooms = bookedPerNight.values.maxOrNull() ?: 0

        return (totalRoom - maxBookedRooms).coerceAtLeast(0)
    }

    override suspend fun createBooking(
        booking: Booking,
        availableRooms: Int
    ): Booking {
        if (availableRooms < 1) throw Exception("Room sold out just now!")

        val docRef = bookingsCollection.document()
        val finalBooking = booking.copy(bookingId = docRef.id)

        docRef.set(finalBooking.toDto()).await()

        return finalBooking
    }

    override suspend fun cancelBooking(bookingId: String): Boolean {
        return try {
            bookingsCollection.document(bookingId)
                .update("status", BookingStatus.CANCELLED.name)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateBooking(booking: Booking, availableRooms: Int): Booking {
        bookingsCollection.document(booking.bookingId).set(booking.toDto()).await()
        return booking
    }

    override suspend fun updateStatus(
        bookingId: String,
        status: BookingStatus
    ): Booking {
        val docRef = bookingsCollection.document(bookingId)

        docRef.update("status", status.name).await()

        val updatedSnapshot = docRef.get().await()
        val updatedBooking = updatedSnapshot.toObject(BookingDto::class.java)
            ?: throw Exception("Booking not found after update")

        return updatedBooking.toDomain()
    }

    override suspend fun getBookingsByUser(userId: String): List<Booking> {
        val snapshot = bookingsCollection
            .whereEqualTo("guestId", userId)
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(BookingDto::class.java)?.toDomain() }
    }

    override suspend fun getBookingsById(bookingId: String): Booking {
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

        val snapshot = bookingsCollection
            .whereEqualTo("hotelId", hotelId)
            .whereEqualTo("roomTypeId", roomTypeId)
            .whereIn("status", statuses.map { it.name })
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { it.toObject(BookingDto::class.java)?.toDomain() }
            .filter {
                it.startDate.seconds < endTs.seconds && it.endDate.seconds > startTs.seconds
            }
    }

    override suspend fun expirePendingBookings() {
        val timeoutMillis = 10 * 60 * 1000L
        val cutoffTime = Timestamp(Date(System.currentTimeMillis() - timeoutMillis))

        try {
            val snapshot = bookingsCollection
                .whereEqualTo("status", "PENDING")
                .whereLessThan("createdAt", cutoffTime)
                .get()
                .await()

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

    // --- Helper Extensions ---
    private fun Timestamp.toLocalDate(): LocalDate {
        return this.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
    }
}