package com.example.hotelbooking.features.hotel.domain.repository

import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    suspend fun getAllHotels(): List<Hotel>
    suspend fun getRecommendHotels(minAverageRating: Double): List<Hotel>
    fun getHotelById(hotelId: String): Flow<Hotel?>

    suspend fun updateHotelRating(hotelId: String, rating: Double)
    suspend fun searchHotels(query: String): List<Hotel>

    // ADMIN
    suspend fun addHotel(adminHotel: AdminHotel)
    fun getHotelsByAdminId(adminId: String): Flow<List<Hotel>>
    fun getAdminHotelById(hotelId: String): Flow<AdminHotel?>
    suspend fun updateHotelStatus(hotelId: String, status: HotelStatus)
    suspend fun updateHotelMinPrice(hotelId: String, minPrice: Double)
    suspend fun searchManagedHotels(adminId: String, query: String): List<Hotel>

    fun clearCache()
}