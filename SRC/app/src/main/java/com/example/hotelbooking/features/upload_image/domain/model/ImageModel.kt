package com.example.hotelbooking.features.upload_image.domain.model

data class ImageModel(
    val id: String = "",
    val publicId: String,
    val imageUrl: String,
    val adminId: String,
    val hotelId: String?,
    val roomId: String?,
    val isUsed: Boolean,
    val createdAt: Long
)