package com.example.hotelbooking.features.recent_viewed.domain.model

import com.example.hotelbooking.features.hotel.domain.model.Hotel

data class RecentWithHotel(
    val viewedAt: Long,
    val hotel: Hotel
)