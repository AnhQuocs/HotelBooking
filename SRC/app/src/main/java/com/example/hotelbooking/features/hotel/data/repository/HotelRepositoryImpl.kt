package com.example.hotelbooking.features.hotel.data.repository

import com.example.hotelbooking.features.hotel.data.mapper.HotelMapper
import com.example.hotelbooking.features.hotel.data.source.FirebaseHotelDataSource
import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.utils.removeAccents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.compareTo

class HotelRepositoryImpl(
    private val dataSource: FirebaseHotelDataSource
) : HotelRepository {

    // USER
    private var cachedHotels: List<Hotel>? = null

    private suspend fun getOrFetchHotels(): List<Hotel> {
        return cachedHotels ?: dataSource.fetchAllHotels()
            .map { (id, dto) -> HotelMapper.dtoToUserHotel(id, dto) }
            .also { cachedHotels = it }
    }

    override suspend fun getAllHotels(): List<Hotel> {
        return getOrFetchHotels()
    }

    override suspend fun getRecommendHotels(minAverageRating: Double): List<Hotel> {
        return getOrFetchHotels().filter { it.averageRating >= minAverageRating }
    }

    override suspend fun getHotelById(hotelId: String): Hotel? {
        val dto = dataSource.fetchHotelById(hotelId) ?: return null
        return HotelMapper.dtoToUserHotel(hotelId, dto)
    }

    override suspend fun updateHotelRating(hotelId: String, rating: Double) {
        dataSource.updateHotelRating(hotelId, rating)
    }

    override suspend fun searchHotels(query: String): List<Hotel> =
        withContext(Dispatchers.Default) {
            val hotels = getOrFetchHotels()
            if (query.isBlank()) return@withContext emptyList()

            val normalizedQuery = query.lowercase().removeAccents()

            hotels.filter { hotel ->
                hotel.name.lowercase().removeAccents().contains(normalizedQuery) ||
                        hotel.address.lowercase().removeAccents().contains(normalizedQuery)
            }
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