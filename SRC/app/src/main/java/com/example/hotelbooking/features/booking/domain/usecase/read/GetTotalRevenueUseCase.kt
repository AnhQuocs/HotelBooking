package com.example.hotelbooking.features.booking.domain.usecase.read

import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

class GetTotalRevenueUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(adminId: String, startDate: Long, endDate: Long, hotelId: String?): Double {
        return repository.getTotalRevenue(adminId, startDate, endDate, hotelId)
    }
}