package com.example.hotelbooking.features.review.data.mapper

import com.example.hotelbooking.features.review.data.dto.ReviewDto
import com.example.tomn_test.features.review.domain.model.Review

fun ReviewDto.toReview(): Review {
    return Review(
        serviceId = serviceId.orEmpty(),
        userId = userId.orEmpty(),
        userName = userName.orEmpty(),
        userProfilePicture = userProfilePicture.orEmpty(),
        serviceType = serviceType.orEmpty(),
        rating = rating ?: 0,
        comment = comment.orEmpty(),
        timestamp = timestamp.orEmpty()
    )
}