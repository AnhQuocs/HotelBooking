package com.example.hotelbooking.features.auth.domain.usecase

import android.net.Uri
import com.example.hotelbooking.features.auth.domain.repository.AuthRepository
import com.example.hotelbooking.features.upload_image.domain.repository.ImageRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateProfileUseCases @Inject constructor(
    val updateSingleFieldUseCase: UpdateSingleFieldUseCase,
    val deleteAccountUseCase: DeleteAccountUseCase,
    val updateAvatarUseCase: UpdateAvatarUseCase
)

class UpdateSingleFieldUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(uid: String, fieldName: String, value: Any) {
        return repository.updateSingleField(uid, fieldName, value)
    }
}

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: String) {
        return repository.deleteAccount(userId)
    }
}

class UpdateAvatarUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(uri: Uri, oldPublicId: String?): Result<String> {
        return try {
            val uploadResult = imageRepository.uploadToCloudinary(uri)
            val (newUrl, newPublicId) = uploadResult.getOrThrow()

            val currentUser = authRepository.getCurrentUser().firstOrNull()
                ?: throw Exception("User not found")

            val updates = mapOf(
                "avatar" to newUrl,
                "avatarPublicId" to newPublicId
            )
            authRepository.updateUserFields(currentUser.uid, updates)

            if (!oldPublicId.isNullOrEmpty()) {
                imageRepository.deleteImageFromCloudinary(oldPublicId)
            }

            Result.success(newUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}