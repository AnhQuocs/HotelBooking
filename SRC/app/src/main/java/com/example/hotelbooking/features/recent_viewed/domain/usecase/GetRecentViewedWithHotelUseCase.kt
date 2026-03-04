package com.example.hotelbooking.features.recent_viewed.domain.usecase

import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.features.recent_viewed.domain.model.RecentWithHotel
import com.example.hotelbooking.features.recent_viewed.domain.repository.RecentViewedRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetRecentViewedWithHotelUseCase @Inject constructor(
    private val recentRepo: RecentViewedRepository,
    private val hotelRepo: HotelRepository
) {
    suspend operator fun invoke(userId: String): List<RecentWithHotel> = coroutineScope {
        val recentList = recentRepo.getRecentViewed(userId)
        if (recentList.isEmpty()) return@coroutineScope emptyList()

        val recentWithHotelList = recentList.map { recent ->
            async {
                val hotel = hotelRepo.getHotelById(recent.id).firstOrNull()

                hotel?.let {
                    RecentWithHotel(
                        viewedAt = recent.viewedAt,
                        hotel = it
                    )
                }
            }
        }.awaitAll().filterNotNull()

        recentWithHotelList.sortedByDescending { it.viewedAt }
    }
}