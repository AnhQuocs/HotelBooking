package com.example.hotelbooking.features.upload_image.domain.usecase

import android.net.Uri
import com.example.hotelbooking.features.upload_image.domain.model.ImageModel
import com.example.hotelbooking.features.upload_image.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageUseCase @Inject constructor(
    val getGalleryUseCase: GetGalleryUseCase,
    val updateImageUsageUseCase: UpdateImageUsageUseCase,
    val uploadToGalleryUseCase: UploadToGalleryUseCase
)

class GetGalleryUseCase @Inject constructor(private val repository: ImageRepository) {
    operator fun invoke(adminId: String): Flow<List<ImageModel>> {
        return repository.getGalleryImages(adminId)
    }
}

class UpdateImageUsageUseCase @Inject constructor(private val repository: ImageRepository) {
    suspend operator fun invoke(
        imageId: String,
        hotelId: String? = null,
        roomId: String? = null
    ): Result<Unit> {
        if (hotelId != null && roomId != null) {
            return Result.failure(Exception("A single photo cannot belong to both the Hotel and the Room simultaneously."))
        }

        return repository.updateImageUsage(imageId, hotelId, roomId)
    }
}

class UploadToGalleryUseCase @Inject constructor(private val repo: ImageRepository) {
    suspend operator fun invoke(uri: Uri, adminId: String): Result<Unit> {
        val uploadResult = repo.uploadToCloudinary(uri)

        return uploadResult.fold(
            onSuccess = { (url, publicId) ->
                val model = ImageModel(
                    imageUrl = url,
                    publicId = publicId,
                    adminId = adminId,
                    hotelId = null,
                    roomId = null,
                    isUsed = false,
                    createdAt = System.currentTimeMillis()
                )
                repo.saveToFirestore(model)
            },
            onFailure = { Result.failure(it) }
        )
    }
}