package com.example.hotelbooking.features.review.data.source

import com.example.hotelbooking.features.review.data.dto.ReviewDto
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseReviewDataSource {
    private val collection = Firebase.firestore.collection("reviews")

    suspend fun fetchReviewsByServiceId(serviceId: String): List<ReviewDto> {
        val snapshot = collection
            .whereEqualTo("serviceId", serviceId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(ReviewDto::class.java) }
    }
}