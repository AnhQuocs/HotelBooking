package com.example.hotelbooking.features.recent_viewed.domain.usecase

import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.features.recent_viewed.domain.model.RecentWithHotel
import com.example.hotelbooking.features.recent_viewed.domain.repository.RecentViewedRepository
import javax.inject.Inject

class GetRecentViewedWithHotelUseCase @Inject constructor(
    private val recentRepo: RecentViewedRepository,
    private val hotelRepo: HotelRepository
) {
    suspend operator fun invoke(userId: String): List<RecentWithHotel> {
        val recentList = recentRepo.getRecentViewed(userId)

        return recentList.mapNotNull { recent ->
            val hotel = hotelRepo.getHotelById(recent.id)
            hotel?.let {
                RecentWithHotel(
                    viewedAt = recent.viewedAt,
                    hotel = it
                )
            }
        }.sortedByDescending { it.viewedAt }
    }
}