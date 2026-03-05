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

    fun fetchHotelById(hotelId: String): Flow<HotelDto?> = callbackFlow {
        val subscription = collection.document(hotelId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close()
                    return@addSnapshotListener
                }

                val dto = snapshot?.toObject(HotelDto::class.java)
                trySend(dto)
            }

        awaitClose { subscription.remove() }
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
                    android.util.Log.w("HotelDataSource", "Hotels listener closed: ${error.message}")

                    close()
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

    suspend fun updateHotelStatus(hotelId: String, status: HotelStatus) {
        collection.document(hotelId)
            .update("status", status.name)
            .await()
    }

    suspend fun updateHotelMinPrice(hotelId: String, minPrice: Double) {
        collection.document(hotelId)
            .update("pricePerNightMin", minPrice)
            .await()
    }
}