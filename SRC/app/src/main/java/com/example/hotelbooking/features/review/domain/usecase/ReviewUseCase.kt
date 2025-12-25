package com.example.hotelbooking.features.review.domain.usecase

import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository

data class ReviewUseCase(
    val getReviewsByServiceIdUseCase: GetReviewsByServiceIdUseCase
)

class GetReviewsByServiceIdUseCase (
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(serviceId: String): List<Review> {
        return repository.getReviewsByServiceId(serviceId)
    }
}