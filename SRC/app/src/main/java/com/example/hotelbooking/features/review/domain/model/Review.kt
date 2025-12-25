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