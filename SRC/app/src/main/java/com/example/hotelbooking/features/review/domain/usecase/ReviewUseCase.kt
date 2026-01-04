package com.example.hotelbooking.features.review.domain.usecase

import com.example.hotelbooking.features.review.domain.model.HotelReviewSummary
import com.example.hotelbooking.features.review.domain.model.RatingStats
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository

data class ReviewUseCase(
    val getHotelReviewSummaryUseCase: GetHotelReviewSummaryUseCase
)

class GetHotelReviewSummaryUseCase (
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(serviceId: String): HotelReviewSummary {
        val reviews = repository.getReviewsByServiceId(serviceId)

        if (reviews.isEmpty()) {
            return HotelReviewSummary(emptyList(), RatingStats())
        }

        val totalCount = reviews.size
        val average = reviews.map { it.rating }.average()

        val percentages = (1..5).associateWith { star ->
            val count = reviews.count { it.rating == star }
            count.toFloat() / totalCount
        }

        return HotelReviewSummary(
            reviews = reviews,
            stats = RatingStats(
                averageRating = average,
                totalReviews = totalCount,
                percentagePerStar = percentages
            )
        )
    }
}