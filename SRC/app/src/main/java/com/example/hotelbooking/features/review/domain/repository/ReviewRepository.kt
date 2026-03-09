package com.example.hotelbooking.features.review.domain.repository

import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.model.ReviewStatus
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    fun getActiveReviewsByServiceId(serviceId: String): Flow<List<Review>>

    fun getAllReviewsForAdmin(serviceId: String): Flow<List<Review>>

    suspend fun createReview(review: Review)

    suspend fun updateReviewStatus(reviewId: String, newStatus: ReviewStatus)
}