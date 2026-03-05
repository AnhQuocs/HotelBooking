package com.example.hotelbooking.features.room.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.usecase.RoomUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RoomListState {
    object Loading : RoomListState()
    object Empty : RoomListState()
    data class Success(val rooms: List<RoomType>) : RoomListState()
    data class Error(val message: String) : RoomListState()
}

@HiltViewModel
class AdminRoomListViewModel @Inject constructor(
    private val roomUseCases: RoomUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<RoomListState>(RoomListState.Loading)
    val uiState: StateFlow<RoomListState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    fun loadRooms(hotelId: String) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            _uiState.value = RoomListState.Loading

            roomUseCases.getRoomsByHotelIdUseCase(hotelId)
                .catch { e ->
                    _uiState.value = RoomListState.Error(e.message ?: "Unknown Error")
                }
                .collect { rooms ->
                    if (rooms.isEmpty()) {
                        _uiState.value = RoomListState.Empty
                    } else {
                        _uiState.value = RoomListState.Success(rooms)
                    }
                }
        }
    }
}