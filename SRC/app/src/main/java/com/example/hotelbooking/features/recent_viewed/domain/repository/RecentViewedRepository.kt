package com.example.hotelbooking.features.recent_viewed.domain.repository

import com.example.hotelbooking.features.recent_viewed.domain.model.RecentViewed

interface RecentViewedRepository {
    suspend fun addRecentViewed(userId: String, recentViewed: RecentViewed)
    suspend fun getRecentViewed(userId: String): List<RecentViewed>
    suspend fun clearRecentViewed(userId: String)
}