package com.example.hotelbooking.features.review.data.dto

import com.google.firebase.firestore.DocumentId

data class ReviewDto(
    @DocumentId val id: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userProfilePicture: String? = null,
    val serviceId: String? = null,
    val serviceType: String? = null,
    val rating: Int? = 0,
    val comment: String? = null,
    val timestamp: String? = null,
    val status: String? = null
)