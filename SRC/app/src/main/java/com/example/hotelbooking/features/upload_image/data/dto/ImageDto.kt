package com.example.hotelbooking.features.upload_image.data.dto

import com.example.hotelbooking.features.upload_image.domain.model.ImageModel

data class ImageDto(
    val id: String = "",
    val imageUrl: String = "",
    val publicId: String = "",
    val adminId: String = "",
    val hotelId: String? = null,
    val roomId: String? = null,
    @field:JvmField val isUsed: Boolean = false,
    val createdAt: Long = 0L
)

fun ImageModel.toDto() = ImageDto(
    id = id,
    imageUrl = imageUrl,
    publicId = publicId,
    adminId = adminId,
    hotelId = hotelId,
    roomId = roomId,
    isUsed = isUsed,
    createdAt = createdAt
)