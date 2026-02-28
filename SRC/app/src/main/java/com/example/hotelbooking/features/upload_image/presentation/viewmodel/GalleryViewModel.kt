package com.example.hotelbooking.features.upload_image.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.upload_image.domain.model.ImageModel
import com.example.hotelbooking.features.upload_image.domain.usecase.ImageUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imageUseCase: ImageUseCase
) : ViewModel() {

    private val _images = MutableStateFlow<List<ImageModel>>(emptyList())
    val images = _images.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    var isUpdating by mutableStateOf(false)
        private set
    var updateError by mutableStateOf<String?>(null)
        private set

    init {
        loadImages()
    }

    private fun loadImages() {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            imageUseCase.getGalleryUseCase(adminId).collect { list ->
                _images.value = list
                _isLoading.value = false
            }
        }
    }

    fun assignImage(
        imageId: String,
        hotelId: String? = null,
        roomId: String? = null,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            isUpdating = true
            updateError = null

            val result = imageUseCase.updateImageUsageUseCase(imageId, hotelId, roomId)

            if (result.isSuccess) {
                isUpdating = false
                onComplete()
            } else {
                isUpdating = false
                updateError = result.exceptionOrNull()?.message ?: "Unknown Error"
            }
        }
    }
}