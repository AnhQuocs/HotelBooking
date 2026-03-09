package com.example.hotelbooking.features.review.domain.usecase

import com.example.hotelbooking.features.review.domain.model.HotelReviewSummary
import com.example.hotelbooking.features.review.domain.model.RatingStats
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ReviewUseCases(
    val getHotelReviewSummaryUseCase: GetHotelReviewSummaryUseCase
)

class GetHotelReviewSummaryUseCase(
    private val repository: ReviewRepository
) {

    operator fun invoke(serviceId: String): Flow<HotelReviewSummary> {
        return repository.getActiveReviewsByServiceId(serviceId)
            .map { reviews ->

                if (reviews.isEmpty()) {
                    return@map HotelReviewSummary(
                        reviews = emptyList(),
                        stats = RatingStats()
                    )
                }

                val totalCount = reviews.size
                val average = reviews.map { it.rating }.average()

                val percentages = (1..5).associateWith { star ->
                    val count = reviews.count { it.rating == star }
                    count.toFloat() / totalCount
                }

                HotelReviewSummary(
                    reviews = reviews,
                    stats = RatingStats(
                        averageRating = average,
                        totalReviews = totalCount,
                        percentagePerStar = percentages
                    )
                )
            }
    }
}