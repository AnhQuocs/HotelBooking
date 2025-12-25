package com.example.hotelbooking.features.review.domain.repository

import com.example.hotelbooking.features.review.domain.model.Review

interface ReviewRepository {
    suspend fun getReviewsByServiceId(serviceId: String): List<Review>
}