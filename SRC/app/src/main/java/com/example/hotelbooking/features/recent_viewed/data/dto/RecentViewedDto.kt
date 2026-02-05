package com.example.hotelbooking.features.recent_viewed.data.dto

import com.example.hotelbooking.features.recent_viewed.domain.model.RecentViewed

data class RecentViewedDto(
    val id: String = "",
    val viewedAt: Long = 0
)

fun RecentViewed.toDto() = RecentViewedDto (
    id = id,
    viewedAt = viewedAt
)

fun RecentViewedDto.toDomain() = RecentViewed(
    id = id,
    viewedAt = viewedAt
)