package com.example.hotelbooking.features.room.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.usecase.AdminRoomUseCases
import com.example.hotelbooking.features.room.domain.usecase.RoomUseCases
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
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
class AdminRoomTypeViewModel @Inject constructor(
    private val roomUseCases: RoomUseCases,
    private val adminRoomUseCases: AdminRoomUseCases
) : ViewModel() {

    private val _roomTypesState = MutableStateFlow<RoomListState>(RoomListState.Loading)
    val roomTypesState: StateFlow<RoomListState> = _roomTypesState.asStateFlow()

    private val _roomDetailState = MutableStateFlow<RoomDetailState>(RoomDetailState.Loading)
    val roomDetailState = _roomDetailState.asStateFlow()

    private var fetchJob: Job? = null
    private var detailJob: Job? = null

    fun loadRooms(hotelId: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _roomTypesState.value = RoomListState.Loading
            roomUseCases.getRoomsByHotelIdUseCase(hotelId)
                .catch { e ->
                    _roomTypesState.value = RoomListState.Error(e.message ?: "Unknown Error")
                }
                .collect { rooms ->
                    _roomTypesState.value = if (rooms.isEmpty()) RoomListState.Empty else RoomListState.Success(rooms)
                }
        }
    }

    fun observeRoomDetail(roomId: String) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            _roomDetailState.value = RoomDetailState.Loading

            adminRoomUseCases.getAdminRoomTypeByIdUseCase(roomId)
                .catch { e ->
                    _roomDetailState.value = RoomDetailState.Error(e.message ?: "Room type not found")
                }
                .collect { room ->
                    if (room != null) {
                        _roomDetailState.value = RoomDetailState.Success(room)
                    } else {
                        _roomDetailState.value = RoomDetailState.Error("Empty data")
                    }
                }
        }
    }

    fun updateStatus(hotelId: String, roomId: String, isActive: Boolean) {
        viewModelScope.launch {
            val newStatus = if (isActive) HotelStatus.ACTIVE else HotelStatus.HIDE

            val result = adminRoomUseCases.updateStatusUseCase(roomId, newStatus)

            result.onSuccess {
                adminRoomUseCases.syncHotelMinPriceUseCase(hotelId)
            }.onFailure {}
        }
    }
}

sealed class RoomDetailState {
    object Loading : RoomDetailState()
    data class Success(val room: RoomType) : RoomDetailState()
    data class Error(val message: String) : RoomDetailState()
}