package com.example.hotelbooking.features.upload_image.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.upload_image.domain.usecase.UploadToGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ImageUploadUiState(
    val isLoading: Boolean = false,
    val selectedUris: List<Uri> = emptyList(),
    val uploadProgress: Int = 0,
    val totalToUpload: Int = 0,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val uploadToGalleryUseCase: UploadToGalleryUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ImageUploadUiState())
        private set

    fun onImagesSelected(uris: List<Uri>) {
        val limitedUris = uris.take(5)
        uiState = uiState.copy(
            selectedUris = limitedUris,
            isSuccess = false,
            error = if (uris.size > 5) "You can only select a maximum of 5 photos." else null
        )
    }

    fun uploadMultipleImages(adminId: String) {
        val uris = uiState.selectedUris
        if (uris.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, totalToUpload = uris.size, uploadProgress = 0, error = null)

            var hasError = false
            uris.forEachIndexed { index, uri ->
                val result = uploadToGalleryUseCase(uri, adminId)

                if (result.isSuccess) {
                    uiState = uiState.copy(uploadProgress = uiState.uploadProgress + 1)
                } else {
                    hasError = true
                    uiState = uiState.copy(error = "Error at image ${index + 1}")
                }
            }

            uiState = uiState.copy(
                isLoading = false,
                isSuccess = !hasError,
                selectedUris = if (!hasError) emptyList() else uiState.selectedUris
            )
        }
    }
}