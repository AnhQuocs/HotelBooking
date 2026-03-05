package com.example.hotelbooking.features.room.data.source

import com.example.hotelbooking.features.room.data.dto.RoomTypeDto
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRoomDataSource {
    private val collection = Firebase.firestore.collection("rooms")

    fun observeRoomsByHotelId(
        hotelId: String
    ): Flow<List<Pair<String, RoomTypeDto>>> = callbackFlow {

        val listenerRegistration = collection
            .whereEqualTo("hotelId", hotelId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    android.util.Log.w("FirestoreDataSource", "Listener closed (likely due to logout): ${error.message}")

                    close()
                    return@addSnapshotListener
                }

                val rooms = snapshot?.documents
                    ?.mapNotNull { doc ->
                        val dto = doc.toObject(RoomTypeDto::class.java)
                        dto?.let { doc.id to it }
                    }
                    ?: emptyList()

                trySend(rooms)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun fetchRoomById(id: String): RoomTypeDto? {
        val doc = collection.document(id).get().await()
        return if (doc.exists()) doc.toObject(RoomTypeDto::class.java) else null
    }

    suspend fun addRoomType(dto: RoomTypeDto): Result<Unit> = try {
        collection.add(dto).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateRoomType(id: String, dto: RoomTypeDto): Result<Unit> = try {
        collection.document(id)
            .set(dto)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeRoomById(id: String): Flow<RoomTypeDto?> = callbackFlow {
        val subscription = collection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.w("FirestoreDataSource", "Room listener error: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                val dto = snapshot?.toObject(RoomTypeDto::class.java)
                trySend(dto)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun updateRoomStatus(id: String, status: String): Result<Unit> = try {
        collection.document(id)
            .update("status", status)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}