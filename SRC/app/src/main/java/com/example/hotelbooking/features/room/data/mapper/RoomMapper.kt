package com.example.hotelbooking.features.room.data.mapper

import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.room.data.dto.AmenityDto
import com.example.hotelbooking.features.room.data.dto.RoomDto
import com.example.hotelbooking.features.room.data.dto.RoomTypeDto
import com.example.hotelbooking.features.room.domain.model.AdminAmenity
import com.example.hotelbooking.features.room.domain.model.AdminRoomType
import com.example.hotelbooking.features.room.domain.model.Amenity
import com.example.hotelbooking.features.room.domain.model.Room
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.utils.LangUtils

fun RoomTypeDto.toRoomType(id: String): RoomType {
    return RoomType(
        id = id,
        hotelId = hotelId.orEmpty(),
        totalRoom = totalStock ?: 0,
        roomList = roomList.map { it.toRoom() },
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
        petPolicy = petPolicy ?: true,
        status = runCatching {
            HotelStatus.valueOf(status)
        }.getOrDefault(HotelStatus.HIDE)
    )
}

fun AmenityDto.toAmenity(): Amenity {
    return Amenity(
        name = LangUtils.getLocalizedText(name),
        iconUrl = iconUrl.orEmpty()
    )
}

fun RoomDto.toRoom(): Room {
    return Room(
        roomNumber = roomNumber.orEmpty()
    )
}

fun RoomType.toDto(): RoomTypeDto {
    return RoomTypeDto(
        hotelId = hotelId,
        totalStock = totalRoom,
        roomList = roomList.map { it.toDto() },
        pricePerNight = pricePerNight,
        capacity = capacity,
        roomSize = roomSize,
        imageUrl = imageUrl,
        name = mapOf("vi" to name),
        description = mapOf("vi" to description),
        bedType = mapOf("vi" to bedType),
        bathroomType = mapOf("vi" to bathroomType),
        amenities = amenities.map { it.toDto() },
        smokingPolicy = smokingPolicy,
        petPolicy = petPolicy,
        status = status.name
    )
}

fun Amenity.toDto(): AmenityDto {
    return AmenityDto(
        name = mapOf("vi" to name),
        iconUrl = iconUrl
    )
}

fun Room.toDto(): RoomDto {
    return RoomDto(
        roomNumber = roomNumber
    )
}

fun RoomTypeDto.toAdminRoomType(id: String): AdminRoomType {
    return AdminRoomType(
        id = id,
        hotelId = hotelId.orEmpty(),
        totalRoom = totalStock ?: 0,
        roomList = roomList.map { Room(it.roomNumber.orEmpty()) },
        pricePerNight = pricePerNight ?: 0,
        capacity = capacity ?: 0,
        roomSize = roomSize ?: 0,
        imageUrl = imageUrl.orEmpty(),
        name = name ?: emptyMap(),
        description = description ?: emptyMap(),
        bedType = bedType ?: emptyMap(),
        bathroomType = bathroomType ?: emptyMap(),
        amenities = amenities.map { AdminAmenity(it.name ?: emptyMap(), it.iconUrl.orEmpty()) },
        smokingPolicy = smokingPolicy ?: false,
        petPolicy = petPolicy ?: true,
        status = runCatching { HotelStatus.valueOf(status) }.getOrDefault(HotelStatus.HIDE)
    )
}

fun AdminRoomType.toDto(): RoomTypeDto {
    return RoomTypeDto(
        hotelId = hotelId,
        totalStock = totalRoom,
        roomList = roomList.map { RoomDto(it.roomNumber) },
        pricePerNight = pricePerNight,
        capacity = capacity,
        roomSize = roomSize,
        imageUrl = imageUrl,
        name = name,
        description = description,
        bedType = bedType,
        bathroomType = bathroomType,
        amenities = amenities.map { AmenityDto(it.name, it.iconUrl) },
        smokingPolicy = smokingPolicy,
        petPolicy = petPolicy,
        status = status.name
    )
}