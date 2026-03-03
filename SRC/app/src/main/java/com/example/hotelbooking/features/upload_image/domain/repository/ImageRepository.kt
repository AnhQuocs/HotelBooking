package com.example.hotelbooking.features.upload_image.domain.repository

import android.net.Uri
import com.example.hotelbooking.features.upload_image.domain.model.ImageModel
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun uploadToCloudinary(uri: Uri): Result<Pair<String, String>>

    suspend fun saveToFirestore(image: ImageModel): Result<Unit>

    fun getGalleryImages(adminId: String): Flow<List<ImageModel>>

    suspend fun updateImageUsage(
        imageId: String,
        hotelId: String?,
        roomId: String?
    ): Result<Unit>

    suspend fun deleteImageFromFirestore(imageId: String): Result<Unit>
}