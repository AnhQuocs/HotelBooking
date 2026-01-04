package com.example.hotelbooking.features.review.domain.model

data class Review(
    val userId: String,
    val userName: String,
    val userProfilePicture: String,
    val serviceId: String,
    val serviceType: String,
    val rating: Int,
    val comment: String,
    val timestamp: String
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