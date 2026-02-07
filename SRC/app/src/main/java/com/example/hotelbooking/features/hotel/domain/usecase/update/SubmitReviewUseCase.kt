package com.example.hotelbooking.features.hotel.domain.usecase.update

import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository
import javax.inject.Inject

class SubmitReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(
        review: Review
    ): Result<Unit> = runCatching {
        reviewRepository.createReview(review)

        hotelRepository.updateHotelRating(
            hotelId = review.serviceId,
            rating = review.rating.toDouble()
        )
    }
}