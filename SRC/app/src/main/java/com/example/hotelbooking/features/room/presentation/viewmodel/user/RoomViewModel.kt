package com.example.hotelbooking.features.room.presentation.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.usecase.RoomUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RoomState<out T> {
    data object Loading : RoomState<Nothing>()
    data class Success<T>(val data: T) : RoomState<T>()
    data class Error(val message: String) : RoomState<Nothing>()
}

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomUseCases: RoomUseCases
) : ViewModel() {

    private val _roomsState = MutableStateFlow<RoomState<List<RoomType>>>(RoomState.Loading)
    val roomsState = _roomsState.asStateFlow()

    private val _roomDetailState = MutableStateFlow<RoomState<RoomType>>(RoomState.Loading)
    val roomDetailState = _roomDetailState.asStateFlow()

    fun loadRooms(hotelId: String) {
        viewModelScope.launch {
            roomUseCases.getRoomsByHotelIdUseCase(hotelId)
                .onStart {
                    _roomsState.value = RoomState.Loading
                }
                .catch { e ->
                    _roomsState.value =
                        RoomState.Error(e.message ?: "Unknown error")
                }
                .collect { rooms ->
                    _roomsState.value = RoomState.Success(rooms)
                }
        }
    }

    fun loadRoomDetail(roomId: String) {
        viewModelScope.launch {
            _roomDetailState.value = RoomState.Loading

            runCatching {
                roomUseCases.getRoomByIdUseCase(roomId = roomId)
            }.onSuccess { roomType ->
                Log.d("RoomViewModel", "$roomType")
                if(roomType != null) {
                    _roomDetailState.value = RoomState.Success(roomType)
                } else {
                    _roomDetailState.value = RoomState.Error("Room not found")
                }
            }.onFailure { e ->
                _roomDetailState.value = RoomState.Error(e.message ?: "Unknown error")
            }
        }
    }
}