package com.example.hotelbooking.features.review.domain.model

enum class ReviewStatus {
    ACTIVE, HIDE
}

data class Review(
    val id: String,
    val userId: String,
    val userName: String,
    val userProfilePicture: String,
    val serviceId: String,
    val serviceType: String,
    val rating: Int,
    val comment: String,
    val timestamp: String,
    val status: ReviewStatus
)

data class RatingStats(
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val percentagePerStar: Map<Int, Float> = emptyMap()
)

data class HotelReviewSummary(
    val reviews: List<Review>,
    val stats: RatingStats
)