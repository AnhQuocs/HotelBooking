package com.example.hotelbooking.features.review.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.review.domain.model.HotelReviewSummary
import com.example.hotelbooking.features.review.domain.usecase.ReviewUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReviewState<out T> {
    data object Loading : ReviewState<Nothing>()
    data class Success<T>(val data: T) : ReviewState<T>()
    data class Error(val message: String) : ReviewState<Nothing>()
}

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewUseCase: ReviewUseCases
) : ViewModel() {

    private val _reviewState =
        MutableStateFlow<ReviewState<HotelReviewSummary>>(ReviewState.Loading)

    val reviewState = _reviewState.asStateFlow()

    fun loadReviews(serviceId: String) {
        viewModelScope.launch {
            reviewUseCase.getHotelReviewSummaryUseCase(serviceId)
                .onStart {
                    _reviewState.value = ReviewState.Loading
                }
                .catch { e ->
                    _reviewState.value = ReviewState.Error(e.message ?: "Unknown error")
                }
                .collect { summary ->
                    _reviewState.value = ReviewState.Success(summary)
                }
        }
    }
}