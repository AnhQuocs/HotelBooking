package com.example.hotelbooking.features.review.data.repository

import com.example.hotelbooking.features.review.data.mapper.toReview
import com.example.hotelbooking.features.review.data.source.FirebaseReviewDataSource
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository

class ReviewRepositoryImpl (
    private val dataSource: FirebaseReviewDataSource
): ReviewRepository {

    override suspend fun getReviewsByServiceId(serviceId: String): List<Review> {
        val dtoList = dataSource.fetchReviewsByServiceId(serviceId)
        return dtoList.map { it.toReview() }
    }
}