package com.example.hotelbooking.features.review.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.review.domain.model.RatingStats
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.model.ReviewStatus
import com.example.hotelbooking.features.review.domain.usecase.AdminReviewUseCases
import com.example.hotelbooking.features.review.domain.usecase.ReviewUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminReviewUiState {
    object Loading : AdminReviewUiState()
    data class Success(
        val reviews: List<Review>,
        val stats: RatingStats
    ) : AdminReviewUiState()

    data class Error(val message: String) : AdminReviewUiState()
}

@HiltViewModel
class AdminReviewViewModel @Inject constructor(
    private val useCases: AdminReviewUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminReviewUiState>(AdminReviewUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadReviews(serviceId: String) {
        _uiState.value = AdminReviewUiState.Loading

        viewModelScope.launch {
            useCases.getAllReviewsForAdminUseCase(serviceId)
                .catch { e ->
                    _uiState.value = AdminReviewUiState.Error(e.message ?: "Failed to load comments")
                }
                .collect { allReviews ->
                    val activeReviews = allReviews.filter { it.status == ReviewStatus.ACTIVE }

                    val stats = if (activeReviews.isEmpty()) {
                        RatingStats()
                    } else {
                        val totalCount = activeReviews.size
                        val average = activeReviews.map { it.rating }.average()
                        val percentages = (1..5).associateWith { star ->
                            activeReviews.count { it.rating == star }.toFloat() / totalCount
                        }
                        RatingStats(average, totalCount, percentages)
                    }

                    _uiState.value = AdminReviewUiState.Success(allReviews, stats)
                }
        }
    }

    fun toggleReviewStatus(review: Review) {
        viewModelScope.launch {
            val newStatus = if (review.status == ReviewStatus.ACTIVE) ReviewStatus.HIDE else ReviewStatus.ACTIVE
            try {
                useCases.updateReviewStatusUseCase(review.id, newStatus)
            } catch (e: Exception) {
            }
        }
    }
}