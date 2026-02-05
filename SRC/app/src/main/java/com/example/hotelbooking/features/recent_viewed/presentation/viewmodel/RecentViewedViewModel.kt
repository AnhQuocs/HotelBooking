package com.example.hotelbooking.features.recent_viewed.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.recent_viewed.domain.model.RecentViewed
import com.example.hotelbooking.features.recent_viewed.domain.model.RecentWithHotel
import com.example.hotelbooking.features.recent_viewed.domain.repository.RecentViewedRepository
import com.example.hotelbooking.features.recent_viewed.domain.usecase.GetRecentViewedWithHotelUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecentViewedUiState {
    object Idle : RecentViewedUiState()
    object Loading : RecentViewedUiState()
    data class Success(val data: List<RecentWithHotel>) : RecentViewedUiState()
    data class Error(val message: String) : RecentViewedUiState()
}

@HiltViewModel
class RecentViewedViewModel @Inject constructor(
    private val recentViewedRepository: RecentViewedRepository,
    private val getRecentViewedWithHotelUseCase: GetRecentViewedWithHotelUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf<RecentViewedUiState>(RecentViewedUiState.Idle)
    val uiState: State<RecentViewedUiState> = _uiState

    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    init {
        refresh()
    }

    fun refresh() {
        val uid = userId ?: run {
            _uiState.value = RecentViewedUiState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = RecentViewedUiState.Loading
            try {
                val data = getRecentViewedWithHotelUseCase(uid)
                _uiState.value = RecentViewedUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = RecentViewedUiState.Error(
                    e.message ?: "Failed to load recent viewed"
                )
            }
        }
    }

    fun addRecentViewed(hotelId: String) {
        val uid = userId ?: return

        viewModelScope.launch {
            try {
                recentViewedRepository.addRecentViewed(
                    uid,
                    RecentViewed(
                        id = hotelId,
                        viewedAt = System.currentTimeMillis()
                    )
                )
                refresh()
            } catch (e: Exception) {
                _uiState.value = RecentViewedUiState.Error(
                    e.message ?: "Failed to add recent viewed"
                )
            }
        }
    }

    fun clearRecentViewed() {
        val uid = userId ?: return

        viewModelScope.launch {
            _uiState.value = RecentViewedUiState.Loading
            try {
                recentViewedRepository.clearRecentViewed(uid)
                _uiState.value = RecentViewedUiState.Success(emptyList())
            } catch (e: Exception) {
                _uiState.value = RecentViewedUiState.Error(
                    e.message ?: "Failed to clear recent viewed"
                )
            }
        }
    }
}