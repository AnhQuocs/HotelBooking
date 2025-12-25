package com.example.hotelbooking.features.room.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.domain.usecase.RoomUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            _roomsState.value = RoomState.Loading

            runCatching {
                roomUseCases.getRoomsByHotelIdUseCase(hotelId = hotelId)
            }.onSuccess { rooms ->
                _roomsState.value = RoomState.Success(rooms)
            }.onFailure { e ->
                _roomsState.value = RoomState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadRoomDetail(roomId: String) {
        viewModelScope.launch {
            _roomDetailState.value = RoomState.Loading

            runCatching {
                roomUseCases.getRoomByIdUseCase(roomId = roomId)
            }.onSuccess { room ->
                Log.d("RoomViewModel", "$room")
                if(room != null) {
                    _roomDetailState.value = RoomState.Success(room)
                } else {
                    _roomDetailState.value = RoomState.Error("Room not found")
                }
            }.onFailure { e ->
                _roomDetailState.value = RoomState.Error(e.message ?: "Unknown error")
            }
        }
    }
}