package com.example.hotelbooking.features.review.data.mapper

import com.example.hotelbooking.features.review.data.dto.ReviewDto
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.model.ReviewStatus

fun ReviewDto.toReview(): Review {
    return Review(
        id = id ?: "",
        serviceId = serviceId.orEmpty(),
        userId = userId.orEmpty(),
        userName = userName.orEmpty(),
        userProfilePicture = userProfilePicture.orEmpty(),
        serviceType = serviceType.orEmpty(),
        rating = rating ?: 0,
        comment = comment.orEmpty(),
        timestamp = timestamp.orEmpty(),
        status = status?.let { ReviewStatus.valueOf(it) } ?: ReviewStatus.ACTIVE
    )
}

fun Review.toDto(): ReviewDto {
    return ReviewDto(
        serviceId = serviceId,
        userId = userId,
        userName = userName,
        userProfilePicture = userProfilePicture,
        serviceType = serviceType,
        rating = rating,
        comment = comment,
        timestamp = timestamp,
        status = status.name
    )
}