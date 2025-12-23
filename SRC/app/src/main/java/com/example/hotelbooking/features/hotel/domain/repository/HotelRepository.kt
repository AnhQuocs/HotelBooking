package com.example.hotelbooking.features.hotel.domain.repository

import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.model.Hotel

interface HotelRepository {
    suspend fun getAllHotels(): List<Hotel>
    suspend fun getRecommendHotels(minAverageRating: Double): List<Hotel>
    suspend fun getHotelById(hotelId: String): Hotel?

    suspend fun updateHotelRating(hotelId: String, rating: Double)

    // ADMIN
    suspend fun addHotel(adminHotel: AdminHotel)
    suspend fun getHotelsByAdminId(adminId: String): List<Hotel>
}