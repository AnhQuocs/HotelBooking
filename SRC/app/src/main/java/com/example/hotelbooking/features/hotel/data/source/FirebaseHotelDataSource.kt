package com.example.hotelbooking.features.hotel.data.source

import com.example.hotelbooking.features.hotel.data.dto.HotelDto
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseHotelDataSource {
    private val collection = Firebase.firestore.collection("hotels")

    suspend fun fetchAllHotels(): List<Pair<String, HotelDto>> {
        return collection
            .whereEqualTo("status", HotelStatus.ACTIVE.name)
            .get()
            .await()
            .map { doc ->
                doc.id to doc.toObject(HotelDto::class.java)
            }
    }

    suspend fun fetchHotelById(hotelId: String): HotelDto? {
        val doc = collection
            .document(hotelId)
            .get()
            .await()

        return doc.toObject(HotelDto::class.java)
    }

    suspend fun updateHotelRating(
        hotelId: String,
        newRating: Double
    ) {
        Firebase.firestore.runTransaction { transaction ->
            val ref = collection.document(hotelId)
            val snapshot = transaction.get(ref)

            val oldCount = snapshot.getLong("numberOfReviews") ?: 0
            val oldAvg = snapshot.getDouble("averageRating") ?: 0.0

            val newCount = oldCount + 1
            val newAvg = ((oldAvg * oldCount) + newRating) / newCount

            transaction.update(
                ref,
                mapOf(
                    "numberOfReviews" to newCount,
                    "averageRating" to newAvg,
                )
            )
        }.await()
    }

    // ADMIN
    suspend fun addHotel(id: String, dto: HotelDto) {
        collection.document(id).set(dto).await()
    }

    fun getHotelsByAdminId(adminId: String): Flow<List<Pair<String, HotelDto>>> = callbackFlow {

        val subscription = collection
            .whereArrayContains("adminIds", adminId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val hotels = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(HotelDto::class.java)?.let { dto ->
                        doc.id to dto
                    }
                } ?: emptyList()

                trySend(hotels)
            }

        awaitClose { subscription.remove() }
    }
}