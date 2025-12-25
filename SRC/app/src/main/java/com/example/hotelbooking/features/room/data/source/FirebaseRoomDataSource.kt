package com.example.hotelbooking.features.room.data.source

import com.example.hotelbooking.features.room.data.dto.RoomTypeDto
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseRoomDataSource {
    private val collection = Firebase.firestore.collection("rooms")

    suspend fun fetchRoomsByHotelId(hotelId: String): List<Pair<String, RoomTypeDto>> {
        return collection
            .whereEqualTo("hotelId", hotelId)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val dto = doc.toObject(RoomTypeDto::class.java)
                dto?.let { doc.id to it }
            }
    }

    suspend fun fetchRoomById(id: String): RoomTypeDto? {
        val doc = collection.document(id).get().await()
        return if (doc.exists()) doc.toObject(RoomTypeDto::class.java) else null
    }
}