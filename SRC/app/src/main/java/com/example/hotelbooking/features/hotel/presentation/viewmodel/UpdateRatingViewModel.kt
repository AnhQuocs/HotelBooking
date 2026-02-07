package com.example.hotelbooking.features.hotel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.usecase.update.SubmitReviewUseCase
import com.example.hotelbooking.features.review.domain.model.Review
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UpdateRatingState {
    data object Idle : UpdateRatingState()
    data object Loading : UpdateRatingState()
    data object Success : UpdateRatingState()
    data class Error(val message: String) : UpdateRatingState()
}

@HiltViewModel
class UpdateRatingViewModel @Inject constructor(
    private val submitReviewUseCase: SubmitReviewUseCase
) : ViewModel() {

    private val _updateRatingState =
        MutableStateFlow<UpdateRatingState>(UpdateRatingState.Idle)
    val updateRatingState = _updateRatingState.asStateFlow()

    fun submitReview(hotelId: String, rating: Double, comment: String) {
        viewModelScope.launch {
            _updateRatingState.value = UpdateRatingState.Loading

            val currentUser = FirebaseAuth.getInstance().currentUser

            val newReview = Review(
                userId = currentUser?.uid ?: "",
                userName = currentUser?.displayName ?: "",
                userProfilePicture = "",
                serviceId = hotelId,
                serviceType = "HOTEL",
                rating = rating.toInt(),
                comment = comment,
                timestamp = Timestamp.now().toString()
            )

            submitReviewUseCase(newReview)
                .onSuccess {
                    _updateRatingState.value = UpdateRatingState.Success
                }
                .onFailure {
                    _updateRatingState.value = UpdateRatingState.Error(it.message ?: "Error")
                }
        }
    }
}