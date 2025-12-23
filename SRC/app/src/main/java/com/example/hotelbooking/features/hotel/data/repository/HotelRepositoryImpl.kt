package com.example.hotelbooking.features.hotel.data.repository

import com.example.hotelbooking.features.hotel.data.mapper.HotelMapper
import com.example.hotelbooking.features.hotel.data.source.FirebaseHotelDataSource
import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository

class HotelRepositoryImpl(
    private val dataSource: FirebaseHotelDataSource
) : HotelRepository {

    // USER

    override suspend fun getAllHotels(): List<Hotel> {
        return dataSource.fetchAllHotels()
            .map { (id, dto) ->
                HotelMapper.dtoToUserHotel(id, dto)
            }
    }

    override suspend fun getRecommendHotels(minAverageRating: Double): List<Hotel> {
        return dataSource.fetchAllHotels()
            .map { (id, dto) ->
                HotelMapper.dtoToUserHotel(id, dto)
            }
            .filter { it.averageRating >= minAverageRating }
    }

    override suspend fun getHotelById(hotelId: String): Hotel? {
        val dto = dataSource.fetchHotelById(hotelId) ?: return null
        return HotelMapper.dtoToUserHotel(hotelId, dto)
    }

    override suspend fun updateHotelRating(hotelId: String, rating: Double) {
        dataSource.updateHotelRating(hotelId, rating)
    }

    // ADMIN

    override suspend fun addHotel(adminHotel: AdminHotel) {
        val dto = HotelMapper.adminHotelToDto(adminHotel)
        dataSource.addHotel(adminHotel.id, dto)
    }

    override suspend fun getHotelsByAdminId(adminId: String): List<Hotel> {
        return dataSource.getHotelsByAdminId(adminId)
            .map { (id, dto) ->
                HotelMapper.dtoToUserHotel(id, dto)
            }
    }
}