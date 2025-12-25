package com.example.hotelbooking.features.room.data.mapper

import com.example.hotelbooking.features.room.data.dto.AmenityDto
import com.example.hotelbooking.features.room.data.dto.RoomTypeDto
import com.example.hotelbooking.features.room.domain.model.Amenity
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.utils.LangUtils

fun RoomTypeDto.toRoomType(id: String): RoomType {
    return RoomType(
        id = id,
        hotelId = hotelId.orEmpty(),
        totalStock = totalStock ?: 0,
        pricePerNight = pricePerNight ?: 0,
        capacity = capacity ?: 0,
        roomSize = roomSize ?: 0,
        imageUrl = imageUrl.orEmpty(),
        name = LangUtils.getLocalizedText(name),
        description = LangUtils.getLocalizedText(description),
        bedType = LangUtils.getLocalizedText(bedType),
        bathroomType = LangUtils.getLocalizedText(bathroomType),
        amenities = amenities.map { it.toAmenity() },
        smokingPolicy = smokingPolicy ?: false,
        petPolicy = petPolicy ?: true
    )
}

fun AmenityDto.toAmenity(): Amenity {
    return Amenity(
        name = LangUtils.getLocalizedText(name),
        iconUrl = iconUrl.orEmpty()
    )
}