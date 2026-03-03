package com.example.hotelbooking.features.hotel.domain.repository

import com.example.hotelbooking.features.hotel.data.dto.HotelDto
import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    suspend fun getAllHotels(): List<Hotel>
    suspend fun getRecommendHotels(minAverageRating: Double): List<Hotel>
    suspend fun getHotelById(hotelId: String): Hotel?

    suspend fun updateHotelRating(hotelId: String, rating: Double)
    suspend fun searchHotels(query: String): List<Hotel>

    // ADMIN
    suspend fun addHotel(adminHotel: AdminHotel)
    fun getHotelsByAdminId(adminId: String): Flow<List<Hotel>>
    suspend fun getAdminHotelById(hotelId: String): AdminHotel?

    fun clearCache()
}