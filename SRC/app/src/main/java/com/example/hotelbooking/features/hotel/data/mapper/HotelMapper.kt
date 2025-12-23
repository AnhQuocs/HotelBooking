package com.example.hotelbooking.features.hotel.data.mapper

import com.example.hotelbooking.features.hotel.data.dto.HotelDto
import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.utils.LangUtils

object HotelMapper {
    fun dtoToUserHotel(id: String, dto: HotelDto): Hotel {
        return Hotel(
            id = id,
            name = LangUtils.getLocalizedText(dto.name),
            description = LangUtils.getLocalizedText(dto.description),
            amenities = LangUtils.getLocalizedList(dto.amenities),
            adminIds = dto.adminIds,
            address = LangUtils.getLocalizedText(dto.address),
            shortAddress = LangUtils.getLocalizedText(dto.shortAddress),
            city = LangUtils.getLocalizedText(dto.city),
            country = LangUtils.getLocalizedText(dto.country),
            thumbnailUrl = dto.thumbnailUrl,
            pricePerNightMin = dto.pricePerNightMin,
            averageRating = dto.averageRating,
            numberOfReviews = dto.numberOfReviews,
            latitude = dto.latitude,
            longitude = dto.longitude,
            checkInTime = dto.checkInTime,
            checkOutTime = dto.checkOutTime
        )
    }

    fun adminHotelToDto(adminHotel: AdminHotel): HotelDto {
        return HotelDto(
            name = adminHotel.rawName,
            description = adminHotel.rawDescription,
            amenities = adminHotel.rawAmenities,
            adminIds = adminHotel.adminIds,
            address = adminHotel.rawAddress,
            shortAddress = adminHotel.rawShortAddress,
            city = adminHotel.rawCity,
            country = adminHotel.rawCountry,
            thumbnailUrl = adminHotel.thumbnailUrl,
            pricePerNightMin = adminHotel.pricePerNightMin,
            latitude = adminHotel.latitude,
            longitude = adminHotel.longitude,
            checkInTime = adminHotel.checkInTime,
            checkOutTime = adminHotel.checkOutTime
        )
    }
}