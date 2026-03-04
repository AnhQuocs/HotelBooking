package com.example.hotelbooking.features.upload_image.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.hotelbooking.features.upload_image.data.dto.ImageDto
import com.example.hotelbooking.features.upload_image.data.dto.toDto
import com.example.hotelbooking.features.upload_image.domain.model.ImageModel
import com.example.hotelbooking.features.upload_image.domain.repository.ImageRepository
import com.example.hotelbooking.features.upload_image.util.FileUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: FirebaseFirestore
) : ImageRepository {

    override suspend fun uploadToCloudinary(uri: Uri): Result<Pair<String, String>> = withContext(
        Dispatchers.IO
    ) {
        try {
            val originalFile = FileUtil.from(context, uri)

            val compressedFile = Compressor.compress(context, originalFile) {
                resolution(1000, 1000)
                quality(80)
                format(Bitmap.CompressFormat.JPEG)
            }

            suspendCancellableCoroutine { cont ->
                MediaManager.get().upload(compressedFile.path)
                    .option("unsigned", true)
                    .option("upload_preset", "hotel_booking_present")
                    .option("folder", "hotel_booking_img")
                    .callback(object : UploadCallback {
                        override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                            val url = resultData?.get("secure_url") as String
                            val publicId = resultData["public_id"] as String
                            cont.resume(Result.success(url to publicId))
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            cont.resume(Result.failure(Exception(error?.description)))
                        }

                        override fun onStart(requestId: String?) {}
                        override fun onProgress(
                            requestId: String?,
                            bytes: Long,
                            totalBytes: Long
                        ) {
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                    }).dispatch()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveToFirestore(image: ImageModel): Result<Unit> = try {
        val docRef = db.collection("images").document()
        val generatedId = docRef.id

        val finalImage = image.copy(id = generatedId)

        docRef.set(finalImage.toDto()).await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getGalleryImages(adminId: String): Flow<List<ImageModel>> = callbackFlow {
        val subscription = db.collection("images")
            .whereEqualTo("adminId", adminId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("GalleryRepo", "Listener closed (likely logout): ${error.message}")

                    close()
                    return@addSnapshotListener
                }

                val images = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ImageDto::class.java)?.let { dto ->
                        ImageModel(
                            id = doc.id,
                            imageUrl = dto.imageUrl,
                            publicId = dto.publicId,
                            adminId = dto.adminId,
                            hotelId = dto.hotelId,
                            roomId = dto.roomId,
                            isUsed = dto.isUsed,
                            createdAt = dto.createdAt
                        )
                    }
                } ?: emptyList()

                trySend(images)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateImageUsage(
        imageId: String,
        hotelId: String?,
        roomId: String?
    ): Result<Unit> = try {
        val batch = db.batch()
        val imagesRef = db.collection("images")

        val oldImagesQuery = when {
            hotelId != null -> imagesRef.whereEqualTo("hotelId", hotelId)
            roomId != null -> imagesRef.whereEqualTo("roomId", roomId)
            else -> null
        }

        oldImagesQuery?.get()?.await()?.documents?.forEach { doc ->
            if (doc.id != imageId) {
                batch.update(doc.reference, mapOf(
                    "hotelId" to null,
                    "roomId" to null,
                    "isUsed" to false
                ))
            }
        }

        val newImageRef = imagesRef.document(imageId)
        batch.update(newImageRef, mapOf(
            "hotelId" to hotelId,
            "roomId" to roomId,
            "isUsed" to (hotelId != null || roomId != null)
        ))

        batch.commit().await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteImageFromFirestore(imageId: String): Result<Unit> = try {
        db.collection("images")
            .document(imageId)
            .delete()
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}