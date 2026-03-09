package com.example.hotelbooking.features.hotel.data.repository

import com.example.hotelbooking.features.hotel.data.mapper.HotelMapper
import com.example.hotelbooking.features.hotel.data.source.FirebaseHotelDataSource
import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.utils.removeAccents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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

    override fun getHotelById(hotelId: String): Flow<Hotel?> {
        return dataSource.fetchHotelById(hotelId).map { dto ->
            dto?.let { HotelMapper.dtoToUserHotel(hotelId, it) }
        }
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

    override suspend fun searchManagedHotels(adminId: String, query: String): List<Hotel> =
        withContext(Dispatchers.Default) {
            try {
                val allHotels = getOrFetchHotels()

                val managedHotels = allHotels.filter { it.adminIds.contains(adminId) }

                if (query.isBlank()) return@withContext managedHotels

                val normalizedQuery = query.lowercase().removeAccents()
                managedHotels.filter { hotel ->
                    hotel.name.lowercase().removeAccents().contains(normalizedQuery) ||
                            hotel.address.lowercase().removeAccents().contains(normalizedQuery)
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

    // ADMIN

    override suspend fun addHotel(adminHotel: AdminHotel) {
        val dto = HotelMapper.adminHotelToDto(adminHotel)
        dataSource.addHotel(adminHotel.id, dto)
    }

    override fun getHotelsByAdminId(adminId: String): Flow<List<Hotel>> {
        return dataSource.getHotelsByAdminId(adminId)
            .map { dtoList ->
                dtoList.map { (id, dto) ->
                    HotelMapper.dtoToUserHotel(id, dto)
                }
            }
    }

    override fun getAdminHotelById(hotelId: String): Flow<AdminHotel?> {
        return dataSource.fetchHotelById(hotelId).map { dto ->
            dto?.let { HotelMapper.dtoToAdminHotel(hotelId, it) }
        }
    }

    override suspend fun updateHotelStatus(hotelId: String, status: HotelStatus) {
        dataSource.updateHotelStatus(hotelId, status)
    }

    override suspend fun updateHotelMinPrice(hotelId: String, minPrice: Double) {
        dataSource.updateHotelMinPrice(hotelId, minPrice)
    }

    override fun clearCache() {
        cachedHotels = null
    }
}