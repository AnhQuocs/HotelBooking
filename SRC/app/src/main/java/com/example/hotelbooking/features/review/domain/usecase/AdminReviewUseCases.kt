package com.example.hotelbooking.features.review.domain.usecase

import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.model.ReviewStatus
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminReviewUseCases @Inject constructor(
    val getAllReviewsForAdminUseCase: GetAllReviewsForAdminUseCase,
    val updateReviewStatusUseCase: UpdateReviewStatusUseCase
)

class GetAllReviewsForAdminUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    operator fun invoke(serviceId: String): Flow<List<Review>> {
        return repository.getAllReviewsForAdmin(serviceId)
    }
}

class UpdateReviewStatusUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(reviewId: String, newStatus: ReviewStatus) {
        repository.updateReviewStatus(reviewId, newStatus)
    }
}