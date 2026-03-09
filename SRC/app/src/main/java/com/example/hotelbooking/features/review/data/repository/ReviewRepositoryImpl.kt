package com.example.hotelbooking.features.review.data.repository

import com.example.hotelbooking.features.review.data.mapper.toDto
import com.example.hotelbooking.features.review.data.mapper.toReview
import com.example.hotelbooking.features.review.data.source.FirebaseReviewDataSource
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.model.ReviewStatus
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseReviewDataSource
) : ReviewRepository {

    override fun getActiveReviewsByServiceId(serviceId: String): Flow<List<Review>> {
        return dataSource.fetchActiveReviews(serviceId)
            .map { dtoList -> dtoList.map { it.toReview() } }
    }

    override fun getAllReviewsForAdmin(serviceId: String): Flow<List<Review>> {
        return dataSource.fetchAllReviewsForAdmin(serviceId)
            .map { dtoList -> dtoList.map { it.toReview() } }
    }

    override suspend fun createReview(review: Review) {
        dataSource.createReview(review.toDto())
    }

    override suspend fun updateReviewStatus(reviewId: String, newStatus: ReviewStatus) {
        dataSource.updateReviewStatus(reviewId, newStatus)
    }
}