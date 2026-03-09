package com.example.hotelbooking.features.review.data.source

import com.example.hotelbooking.features.review.data.dto.ReviewDto
import com.example.hotelbooking.features.review.domain.model.ReviewStatus
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseReviewDataSource {

    private val collection = Firebase.firestore.collection("reviews")

    suspend fun createReview(review: ReviewDto) {
        collection
            .add(review)
            .await()
    }

    fun fetchActiveReviews(serviceId: String): Flow<List<ReviewDto>> = callbackFlow {
        val query = collection
            .whereEqualTo("serviceId", serviceId)
            .whereEqualTo("status", ReviewStatus.ACTIVE.name)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            snapshot?.let {
                val reviews = it.documents.mapNotNull { doc -> doc.toObject(ReviewDto::class.java) }
                trySend(reviews)
            }
        }
        awaitClose { listener.remove() }
    }

    fun fetchAllReviewsForAdmin(serviceId: String): Flow<List<ReviewDto>> = callbackFlow {
        val query = collection.whereEqualTo("serviceId", serviceId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            snapshot?.let {
                val reviews = it.documents.mapNotNull { doc -> doc.toObject(ReviewDto::class.java) }
                trySend(reviews)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateReviewStatus(reviewId: String, newStatus: ReviewStatus) {
        collection.document(reviewId)
            .update("status", newStatus.name)
            .await()
    }
}