package com.example.hotelbooking.features.recent_viewed.data.repository

import com.example.hotelbooking.features.recent_viewed.data.dto.RecentViewedDto
import com.example.hotelbooking.features.recent_viewed.data.dto.toDomain
import com.example.hotelbooking.features.recent_viewed.data.dto.toDto
import com.example.hotelbooking.features.recent_viewed.domain.model.RecentViewed
import com.example.hotelbooking.features.recent_viewed.domain.repository.RecentViewedRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class RecentViewedRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RecentViewedRepository {

    override suspend fun addRecentViewed(
        userId: String,
        recentViewed: RecentViewed
    ) {
        val userCollection = firestore.collection("users")
            .document(userId)
            .collection("recentViewed")

        val snapshot = userCollection
            .orderBy("viewedAt", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .await()

        val existingDoc = snapshot.documents.find { it.id == recentViewed.id }
        if (existingDoc != null) {
            existingDoc.reference
                .update("viewedAt", recentViewed.viewedAt)
                .await()
            return
        }

        if (snapshot.size() >= 3) {
            val oldest = snapshot.documents.last()
            oldest.reference.delete().await()
        }

        userCollection
            .document(recentViewed.id)
            .set(recentViewed.toDto())
            .await()
    }

    override suspend fun getRecentViewed(userId: String): List<RecentViewed> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("recentViewed")
            .orderBy("viewedAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val list = snapshot.documents.mapNotNull {
            it.toObject(RecentViewedDto::class.java)?.toDomain()
        }
        return list
    }

    override suspend fun clearRecentViewed(userId: String) {
        val batch = firestore.batch()
        val collection = firestore.collection("users")
            .document(userId)
            .collection("recentViewed")
            .get()
            .await()

        collection.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }
}