package com.example.hotelbooking.features.hotel.domain.usecase.read

import com.example.hotelbooking.features.hotel.data.dto.HotelDto
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow

class GetHotelsByAdminIdUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(adminId: String): Flow<List<Hotel>> {
        return repository.getHotelsByAdminId(adminId)
    }
}